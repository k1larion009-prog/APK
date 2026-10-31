package com.mochipet.widget.domain

import com.mochipet.widget.data.PetAction
import com.mochipet.widget.data.PetMood
import com.mochipet.widget.data.PetSnapshot
import com.mochipet.widget.data.PetStats
import kotlin.random.Random

object PetBehaviorEngine {

    private const val MIN_MOOD_DWELL_MS = 6 * 60 * 1000L
    private const val REACTION_DURATION_MS = 90 * 1000L
    private const val BORED_THRESHOLD_MS = 5 * 60 * 60 * 1000L
    private const val MAX_DECAY_MINUTES = 12 * 60f

    fun advance(
        previous: PetSnapshot,
        nowEpochMs: Long,
        action: PetAction?,
        random: Random = Random.Default
    ): PetSnapshot {
        val elapsedMinutes = ((nowEpochMs - previous.lastUpdateEpochMs).coerceAtLeast(0L) / 60000f)
            .coerceAtMost(MAX_DECAY_MINUTES)

        val wasSleeping = previous.mood == PetMood.SLEEPING
        val decayedStats = decay(previous.stats, elapsedMinutes, wasSleeping)

        val statsAfterAction = if (action != null) {
            applyAction(decayedStats, action)
        } else {
            decayedStats
        }.clamped()

        val lastInteraction = if (action != null) nowEpochMs else previous.lastInteractionEpochMs
        val interactionCount = if (action != null) previous.interactionCount + 1 else previous.interactionCount

        val dwellMs = nowEpochMs - previous.lastMoodChangeEpochMs
        val forcedActive = nowEpochMs < previous.forcedMoodUntilEpochMs

        val resolvedMood: PetMood
        var newMoodChangeEpochMs = previous.lastMoodChangeEpochMs
        var newForcedUntil = previous.forcedMoodUntilEpochMs

        when {
            action != null -> {
                resolvedMood = action.toForcedMood(previous.mood)
                newMoodChangeEpochMs = nowEpochMs
                newForcedUntil = nowEpochMs + REACTION_DURATION_MS
            }
            forcedActive -> {
                resolvedMood = previous.mood
            }
            else -> {
                val candidate = decideMood(
                    stats = statsAfterAction,
                    previousMood = previous.mood,
                    minutesSinceInteraction = (nowEpochMs - lastInteraction) / 60000f,
                    dwellMs = dwellMs,
                    random = random
                )
                if (candidate != previous.mood && dwellMs >= MIN_MOOD_DWELL_MS) {
                    resolvedMood = candidate
                    newMoodChangeEpochMs = nowEpochMs
                } else {
                    resolvedMood = previous.mood
                }
            }
        }

        return PetSnapshot(
            stats = statsAfterAction,
            mood = resolvedMood,
            lastUpdateEpochMs = nowEpochMs,
            lastInteractionEpochMs = lastInteraction,
            lastMoodChangeEpochMs = newMoodChangeEpochMs,
            interactionCount = interactionCount,
            forcedMoodUntilEpochMs = newForcedUntil
        )
    }

    private fun decay(stats: PetStats, minutes: Float, wasSleeping: Boolean): PetStats {
        if (minutes <= 0f) return stats
        val hunger = stats.hunger + minutes * 0.12f
        val sleepiness = if (wasSleeping) {
            stats.sleepiness - minutes * 0.55f
        } else {
            stats.sleepiness + minutes * 0.09f
        }
        val energy = if (wasSleeping) {
            stats.energy + minutes * 0.45f
        } else {
            stats.energy - minutes * 0.06f
        }
        val curiosity = stats.curiosity + minutes * 0.07f
        val affection = stats.affection - minutes * 0.02f
        val hungerPenalty = if (hunger > 70f) (hunger - 70f) * 0.05f else 0f
        val energyPenalty = if (energy < 30f) (30f - energy) * 0.04f else 0f
        val moodDrift = (50f - stats.mood) * 0.01f * minutes
        val mood = stats.mood + moodDrift - hungerPenalty - energyPenalty
        return PetStats(
            energy = energy,
            mood = mood,
            hunger = hunger,
            curiosity = curiosity,
            affection = affection,
            sleepiness = sleepiness
        ).clamped()
    }

    private fun applyAction(stats: PetStats, action: PetAction): PetStats {
        return when (action) {
            PetAction.FEED -> stats.copy(
                hunger = stats.hunger - 35f,
                mood = stats.mood + 8f,
                energy = stats.energy + 6f
            )
            PetAction.PLAY -> stats.copy(
                mood = stats.mood + 14f,
                energy = stats.energy - 10f,
                curiosity = stats.curiosity - 25f,
                affection = stats.affection + 4f
            )
            PetAction.PET -> stats.copy(
                affection = stats.affection + 16f,
                mood = stats.mood + 10f
            )
            PetAction.WAKE -> stats.copy(
                sleepiness = stats.sleepiness - 20f,
                mood = stats.mood - 3f
            )
            PetAction.TAP -> stats.copy(
                mood = stats.mood + 3f,
                curiosity = stats.curiosity - 5f
            )
        }.clamped()
    }

    private fun decideMood(
        stats: PetStats,
        previousMood: PetMood,
        minutesSinceInteraction: Float,
        dwellMs: Long,
        random: Random
    ): PetMood {
        if (stats.sleepiness >= 82f) {
            return PetMood.SLEEPING
        }
        if (previousMood == PetMood.SLEEPING) {
            return if (stats.sleepiness < 45f) PetMood.WAKING else PetMood.SLEEPING
        }
        if (previousMood == PetMood.WAKING) {
            return PetMood.IDLE
        }
        if (stats.energy <= 18f) {
            return PetMood.RESTING
        }
        if (stats.hunger >= 88f) {
            return PetMood.ANGRY
        }
        if (stats.hunger >= 70f) {
            return PetMood.SAD
        }
        if (stats.mood <= 22f) {
            return PetMood.SAD
        }
        if (stats.curiosity >= 80f) {
            return PetMood.CURIOUS
        }
        if (minutesSinceInteraction * 60000f >= BORED_THRESHOLD_MS && stats.mood < 60f) {
            return PetMood.BORED
        }
        if (stats.mood <= 38f) {
            return PetMood.BORED
        }
        if (stats.mood >= 72f && stats.affection >= 55f) {
            return PetMood.HAPPY
        }
        if (dwellMs >= MIN_MOOD_DWELL_MS * 3 && random.nextFloat() < 0.08f) {
            return if (random.nextBoolean()) PetMood.WATCHING else PetMood.SURPRISED
        }
        return PetMood.IDLE
    }

    private fun PetAction.toForcedMood(previousMood: PetMood): PetMood = when (this) {
        PetAction.FEED -> PetMood.EATING
        PetAction.PLAY -> PetMood.PLAYING
        PetAction.PET -> PetMood.INTERACTING
        PetAction.WAKE -> PetMood.WAKING
        PetAction.TAP -> if (previousMood == PetMood.SLEEPING) PetMood.WAKING else PetMood.SURPRISED
    }
}

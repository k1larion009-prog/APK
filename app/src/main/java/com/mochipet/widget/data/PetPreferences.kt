package com.mochipet.widget.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.petDataStore by preferencesDataStore(name = "mochi_pet_state")

class PetPreferences(private val context: Context) {

    private object Keys {
        val ENERGY = floatPreferencesKey("energy")
        val MOOD_VALUE = floatPreferencesKey("mood_value")
        val HUNGER = floatPreferencesKey("hunger")
        val CURIOSITY = floatPreferencesKey("curiosity")
        val AFFECTION = floatPreferencesKey("affection")
        val SLEEPINESS = floatPreferencesKey("sleepiness")
        val MOOD_STATE = stringPreferencesKey("mood_state")
        val LAST_UPDATE = longPreferencesKey("last_update")
        val LAST_INTERACTION = longPreferencesKey("last_interaction")
        val LAST_MOOD_CHANGE = longPreferencesKey("last_mood_change")
        val INTERACTION_COUNT = longPreferencesKey("interaction_count")
        val FORCED_MOOD_UNTIL = longPreferencesKey("forced_mood_until")
    }

    suspend fun load(nowEpochMs: Long): PetSnapshot {
        val prefs = context.petDataStore.data.first()
        val hasState = prefs.contains(Keys.LAST_UPDATE)
        if (!hasState) {
            return PetSnapshot.initial(nowEpochMs)
        }
        val moodName = prefs[Keys.MOOD_STATE] ?: PetMood.IDLE.name
        val mood = runCatching { PetMood.valueOf(moodName) }.getOrDefault(PetMood.IDLE)
        return PetSnapshot(
            stats = PetStats(
                energy = prefs[Keys.ENERGY] ?: 80f,
                mood = prefs[Keys.MOOD_VALUE] ?: 65f,
                hunger = prefs[Keys.HUNGER] ?: 30f,
                curiosity = prefs[Keys.CURIOSITY] ?: 50f,
                affection = prefs[Keys.AFFECTION] ?: 50f,
                sleepiness = prefs[Keys.SLEEPINESS] ?: 20f
            ),
            mood = mood,
            lastUpdateEpochMs = prefs[Keys.LAST_UPDATE] ?: nowEpochMs,
            lastInteractionEpochMs = prefs[Keys.LAST_INTERACTION] ?: nowEpochMs,
            lastMoodChangeEpochMs = prefs[Keys.LAST_MOOD_CHANGE] ?: nowEpochMs,
            interactionCount = prefs[Keys.INTERACTION_COUNT] ?: 0L,
            forcedMoodUntilEpochMs = prefs[Keys.FORCED_MOOD_UNTIL] ?: 0L
        )
    }

    suspend fun save(snapshot: PetSnapshot) {
        context.petDataStore.edit { prefs ->
            prefs[Keys.ENERGY] = snapshot.stats.energy
            prefs[Keys.MOOD_VALUE] = snapshot.stats.mood
            prefs[Keys.HUNGER] = snapshot.stats.hunger
            prefs[Keys.CURIOSITY] = snapshot.stats.curiosity
            prefs[Keys.AFFECTION] = snapshot.stats.affection
            prefs[Keys.SLEEPINESS] = snapshot.stats.sleepiness
            prefs[Keys.MOOD_STATE] = snapshot.mood.name
            prefs[Keys.LAST_UPDATE] = snapshot.lastUpdateEpochMs
            prefs[Keys.LAST_INTERACTION] = snapshot.lastInteractionEpochMs
            prefs[Keys.LAST_MOOD_CHANGE] = snapshot.lastMoodChangeEpochMs
            prefs[Keys.INTERACTION_COUNT] = snapshot.interactionCount
            prefs[Keys.FORCED_MOOD_UNTIL] = snapshot.forcedMoodUntilEpochMs
        }
    }
}

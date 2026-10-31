package com.mochipet.widget.data

data class PetSnapshot(
    val stats: PetStats,
    val mood: PetMood,
    val lastUpdateEpochMs: Long,
    val lastInteractionEpochMs: Long,
    val lastMoodChangeEpochMs: Long,
    val interactionCount: Long,
    val forcedMoodUntilEpochMs: Long
) {
    companion object {
        fun initial(nowEpochMs: Long): PetSnapshot = PetSnapshot(
            stats = PetStats.initial(),
            mood = PetMood.IDLE,
            lastUpdateEpochMs = nowEpochMs,
            lastInteractionEpochMs = nowEpochMs,
            lastMoodChangeEpochMs = nowEpochMs,
            interactionCount = 0L,
            forcedMoodUntilEpochMs = 0L
        )
    }
}

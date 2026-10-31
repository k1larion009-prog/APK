package com.mochipet.widget.data

import kotlin.math.roundToInt

data class PetStats(
    val energy: Float,
    val mood: Float,
    val hunger: Float,
    val curiosity: Float,
    val affection: Float,
    val sleepiness: Float
) {
    fun clamped(): PetStats = PetStats(
        energy = energy.coerceIn(0f, 100f),
        mood = mood.coerceIn(0f, 100f),
        hunger = hunger.coerceIn(0f, 100f),
        curiosity = curiosity.coerceIn(0f, 100f),
        affection = affection.coerceIn(0f, 100f),
        sleepiness = sleepiness.coerceIn(0f, 100f)
    )

    fun energyPercent(): Int = energy.roundToInt()
    fun moodPercent(): Int = mood.roundToInt()
    fun hungerPercent(): Int = hunger.roundToInt()
    fun curiosityPercent(): Int = curiosity.roundToInt()
    fun affectionPercent(): Int = affection.roundToInt()
    fun sleepinessPercent(): Int = sleepiness.roundToInt()

    companion object {
        fun initial(): PetStats = PetStats(
            energy = 80f,
            mood = 65f,
            hunger = 30f,
            curiosity = 50f,
            affection = 50f,
            sleepiness = 20f
        )
    }
}

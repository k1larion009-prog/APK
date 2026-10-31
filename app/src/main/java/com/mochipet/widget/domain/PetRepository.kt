package com.mochipet.widget.domain

import android.content.Context
import com.mochipet.widget.data.PetAction
import com.mochipet.widget.data.PetPreferences
import com.mochipet.widget.data.PetSnapshot

class PetRepository(context: Context) {

    private val preferences = PetPreferences(context.applicationContext)

    suspend fun currentSnapshot(nowEpochMs: Long): PetSnapshot {
        return preferences.load(nowEpochMs)
    }

    suspend fun tick(nowEpochMs: Long): PetSnapshot {
        val previous = preferences.load(nowEpochMs)
        val next = PetBehaviorEngine.advance(previous, nowEpochMs, action = null)
        preferences.save(next)
        return next
    }

    suspend fun applyAction(action: PetAction, nowEpochMs: Long): PetSnapshot {
        val previous = preferences.load(nowEpochMs)
        val next = PetBehaviorEngine.advance(previous, nowEpochMs, action = action)
        preferences.save(next)
        return next
    }
}

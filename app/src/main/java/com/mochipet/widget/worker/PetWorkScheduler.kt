package com.mochipet.widget.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PetWorkScheduler {

    private const val PERIODIC_WORK_NAME = "mochi_pet_periodic_tick"
    private const val REACTION_WORK_NAME = "mochi_pet_reaction_revert"

    fun schedulePeriodic(context: Context) {
        val request = PeriodicWorkRequestBuilder<PetPeriodicWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelPeriodic(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
    }

    fun scheduleReactionRevert(context: Context) {
        val request = OneTimeWorkRequestBuilder<PetReactionRevertWorker>()
            .setInitialDelay(90, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            REACTION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}

package com.mochipet.widget.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mochipet.widget.widget.PetWidgetProvider

class PetPeriodicWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            PetWidgetProvider.refreshFromTick(applicationContext)
            Result.success()
        } catch (error: Exception) {
            Result.retry()
        }
    }
}

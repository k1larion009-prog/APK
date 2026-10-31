package com.mochipet.widget.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.mochipet.widget.data.PetAction
import com.mochipet.widget.domain.PetRepository
import com.mochipet.widget.widget.PetWidgetProvider

class PetUpdateActionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val actionName = inputData.getString(KEY_ACTION) ?: return Result.failure()
        val action = runCatching { PetAction.valueOf(actionName) }.getOrNull() ?: return Result.failure()

        return try {
            val repository = PetRepository(applicationContext)
            val snapshot = repository.applyAction(action, System.currentTimeMillis())
            val ids = PetWidgetProvider.widgetIds(applicationContext)
            PetWidgetProvider.pushSnapshot(applicationContext, ids, snapshot)
            Result.success()
        } catch (error: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val KEY_WIDGET_ID = "widget_id"
        private const val KEY_ACTION = "action"

        fun buildInput(widgetId: Int, action: PetAction): Data =
            Data.Builder()
                .putInt(KEY_WIDGET_ID, widgetId)
                .putString(KEY_ACTION, action.name)
                .build()
    }
}

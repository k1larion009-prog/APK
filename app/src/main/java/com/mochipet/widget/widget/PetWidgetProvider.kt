package com.mochipet.widget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mochipet.widget.data.PetAction
import com.mochipet.widget.data.PetSnapshot
import com.mochipet.widget.domain.PetRepository
import com.mochipet.widget.worker.PetPeriodicWorker
import com.mochipet.widget.worker.PetUpdateActionWorker
import com.mochipet.widget.worker.PetWorkScheduler

class PetWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        refreshWidgets(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        refreshWidgets(context)
    }

    override fun onEnabled(context: Context) {
        PetWorkScheduler.schedulePeriodic(context)
    }

    override fun onDisabled(context: Context) {
        PetWorkScheduler.cancelPeriodic(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action ?: return
        val petAction = when (action) {
            WidgetRenderer.ACTION_TAP -> PetAction.TAP
            WidgetRenderer.ACTION_FEED -> PetAction.FEED
            WidgetRenderer.ACTION_PLAY -> PetAction.PLAY
            WidgetRenderer.ACTION_PET -> PetAction.PET
            WidgetRenderer.ACTION_WAKE -> PetAction.WAKE
            else -> null
        }
        val widgetId = intent.getIntExtra(WidgetRenderer.EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (petAction != null && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            val request = OneTimeWorkRequestBuilder<PetUpdateActionWorker>()
                .setInputData(PetUpdateActionWorker.buildInput(widgetId, petAction))
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "mochi_action_$widgetId",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
            PetWorkScheduler.scheduleReactionRevert(context)
        }
    }

    companion object {

        fun widgetIds(context: Context): IntArray {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, PetWidgetProvider::class.java)
            return manager.getAppWidgetIds(component)
        }

        suspend fun refreshFromTick(context: Context) {
            val ids = widgetIds(context)
            if (ids.isEmpty()) return
            val repository = PetRepository(context)
            val snapshot = repository.tick(System.currentTimeMillis())
            pushSnapshot(context, ids, snapshot)
        }

        fun pushSnapshot(
            context: Context,
            appWidgetIds: IntArray,
            snapshot: PetSnapshot
        ) {
            if (appWidgetIds.isEmpty()) return
            val manager = AppWidgetManager.getInstance(context)
            for (id in appWidgetIds) {
                val category = WidgetSizeProvider.categoryFor(context, manager, id)
                val views = WidgetRenderer.render(context, id, category, snapshot)
                manager.updateAppWidget(id, views)
            }
        }

        fun refreshWidgets(context: Context) {
            val request = OneTimeWorkRequestBuilder<PetPeriodicWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}

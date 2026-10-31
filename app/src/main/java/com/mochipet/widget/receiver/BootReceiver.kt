package com.mochipet.widget.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mochipet.widget.widget.PetWidgetProvider
import com.mochipet.widget.worker.PetWorkScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val ids = PetWidgetProvider.widgetIds(context)
            if (ids.isNotEmpty()) {
                PetWorkScheduler.schedulePeriodic(context)
                PetWidgetProvider.refreshWidgets(context)
            }
        }
    }
}

package com.mochipet.widget.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle

enum class WidgetSizeCategory {
    COMPACT,
    LARGE
}

object WidgetSizeProvider {

    private const val LARGE_WIDTH_THRESHOLD_DP = 180

    fun categoryFor(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int): WidgetSizeCategory {
        val options: Bundle = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
        return if (minWidth >= LARGE_WIDTH_THRESHOLD_DP) {
            WidgetSizeCategory.LARGE
        } else {
            WidgetSizeCategory.COMPACT
        }
    }
}

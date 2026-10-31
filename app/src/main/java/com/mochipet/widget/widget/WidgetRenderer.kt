package com.mochipet.widget.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.mochipet.widget.R
import com.mochipet.widget.data.PetMood
import com.mochipet.widget.data.PetSnapshot

object WidgetRenderer {

    const val ACTION_TAP = "com.mochipet.widget.ACTION_TAP"
    const val ACTION_FEED = "com.mochipet.widget.ACTION_FEED"
    const val ACTION_PLAY = "com.mochipet.widget.ACTION_PLAY"
    const val ACTION_PET = "com.mochipet.widget.ACTION_PET"
    const val ACTION_WAKE = "com.mochipet.widget.ACTION_WAKE"
    const val EXTRA_WIDGET_ID = "extra_widget_id"

    fun render(
        context: Context,
        appWidgetId: Int,
        sizeCategory: WidgetSizeCategory,
        snapshot: PetSnapshot
    ): RemoteViews {
        val layoutRes = when (sizeCategory) {
            WidgetSizeCategory.COMPACT -> R.layout.widget_pet_compact
            WidgetSizeCategory.LARGE -> R.layout.widget_pet_large
        }
        val views = RemoteViews(context.packageName, layoutRes)

        views.setImageViewResource(R.id.petImage, drawableFor(snapshot.mood))
        views.setContentDescription(R.id.petImage, context.getString(moodDescriptionRes(snapshot.mood)))

        views.setProgressBar(R.id.energyBar, 100, snapshot.stats.energyPercent(), false)
        views.setProgressBar(R.id.moodBar, 100, snapshot.stats.moodPercent(), false)
        views.setProgressBar(R.id.hungerBar, 100, snapshot.stats.hungerPercent(), false)

        views.setOnClickPendingIntent(R.id.petImage, pendingIntentFor(context, appWidgetId, ACTION_TAP))
        views.setOnClickPendingIntent(R.id.feedButton, pendingIntentFor(context, appWidgetId, ACTION_FEED))
        views.setOnClickPendingIntent(R.id.playButton, pendingIntentFor(context, appWidgetId, ACTION_PLAY))
        views.setOnClickPendingIntent(R.id.petButton, pendingIntentFor(context, appWidgetId, ACTION_PET))

        return views
    }

    private fun pendingIntentFor(context: Context, appWidgetId: Int, action: String): PendingIntent {
        val intent = Intent(context, PetWidgetProvider::class.java).apply {
            this.action = action
            putExtra(EXTRA_WIDGET_ID, appWidgetId)
            data = Uri.parse("mochipet://widget/$appWidgetId/$action")
        }
        val requestCode = (appWidgetId.toString() + action).hashCode()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun drawableFor(mood: PetMood): Int = when (mood) {
        PetMood.IDLE -> R.drawable.pet_idle
        PetMood.SLEEPING -> R.drawable.pet_sleeping
        PetMood.WAKING -> R.drawable.pet_waking
        PetMood.HAPPY -> R.drawable.pet_happy
        PetMood.SAD -> R.drawable.pet_sad
        PetMood.CURIOUS -> R.drawable.pet_curious
        PetMood.BORED -> R.drawable.pet_bored
        PetMood.SURPRISED -> R.drawable.pet_surprised
        PetMood.ANGRY -> R.drawable.pet_angry
        PetMood.PLAYING -> R.drawable.pet_playing
        PetMood.EATING -> R.drawable.pet_eating
        PetMood.RESTING -> R.drawable.pet_resting
        PetMood.WATCHING -> R.drawable.pet_watching
        PetMood.INTERACTING -> R.drawable.pet_interacting
    }

    private fun moodDescriptionRes(mood: PetMood): Int = when (mood) {
        PetMood.IDLE -> R.string.mood_idle
        PetMood.SLEEPING -> R.string.mood_sleeping
        PetMood.WAKING -> R.string.mood_waking
        PetMood.HAPPY -> R.string.mood_happy
        PetMood.SAD -> R.string.mood_sad
        PetMood.CURIOUS -> R.string.mood_curious
        PetMood.BORED -> R.string.mood_bored
        PetMood.SURPRISED -> R.string.mood_surprised
        PetMood.ANGRY -> R.string.mood_angry
        PetMood.PLAYING -> R.string.mood_playing
        PetMood.EATING -> R.string.mood_eating
        PetMood.RESTING -> R.string.mood_resting
        PetMood.WATCHING -> R.string.mood_watching
        PetMood.INTERACTING -> R.string.mood_interacting
    }
}

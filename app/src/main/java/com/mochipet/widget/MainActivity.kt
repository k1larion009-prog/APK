package com.mochipet.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mochipet.widget.widget.PetWidgetProvider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.statusText)
        val pinButton = findViewById<Button>(R.id.pinWidgetButton)

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, PetWidgetProvider::class.java)
        val canPinDirectly = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            appWidgetManager.isRequestPinAppWidgetSupported

        if (canPinDirectly) {
            pinButton.setOnClickListener {
                appWidgetManager.requestPinAppWidget(provider, null, null)
            }
            statusText.text = getString(R.string.onboarding_pin_available)
        } else {
            pinButton.setOnClickListener {
                Toast.makeText(this, R.string.onboarding_manual_add, Toast.LENGTH_LONG).show()
            }
            statusText.text = getString(R.string.onboarding_manual_add)
        }
    }
}

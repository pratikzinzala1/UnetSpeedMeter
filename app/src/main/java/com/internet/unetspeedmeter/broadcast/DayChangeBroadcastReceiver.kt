package com.internet.unetspeedmeter.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.text.SimpleDateFormat
import java.util.*


abstract class DayChangedBroadcastReceiver : BroadcastReceiver() {

    private var date = Date()
    private val dateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    private var previousDate = ""

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        val currentDate = Date()

        if ((action == Intent.ACTION_DATE_CHANGED) && !isSameDay(
                currentDate
            )
        ) {
            previousDate = dateFormat.format(date)
            date = currentDate
            onDayChanged(previousDate)
        }
    }

    private fun isSameDay(currentDate: Date) =
        dateFormat.format(currentDate) == dateFormat.format(date)

    abstract fun onDayChanged(previousDate: String)

    companion object {

        fun getIntentFilter() = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
        }
    }
}

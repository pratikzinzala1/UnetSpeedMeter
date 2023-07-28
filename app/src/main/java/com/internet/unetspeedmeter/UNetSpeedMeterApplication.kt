package com.internet.unetspeedmeter

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.internet.unetspeedmeter.data.AppDataContainer



class UNetSpeedMeterApplication:Application() {

    lateinit var appDataContainer:AppDataContainer

    override fun onCreate() {
        super.onCreate()
        appDataContainer = AppDataContainer(applicationContext)
        //DynamicColors.applyToActivitiesIfAvailable(this)
    }

}
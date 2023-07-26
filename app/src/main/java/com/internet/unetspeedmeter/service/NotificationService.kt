package com.internet.unetspeedmeter.service

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.internet.unetspeedmeter.*
import com.internet.unetspeedmeter.broadcast.DayChangedBroadcastReceiver
import com.internet.unetspeedmeter.data.InternetDataItem
import com.internet.unetspeedmeter.math.Math
import com.internet.unetspeedmeter.math.icon
import com.internet.unetspeedmeter.math.kbToString
import com.internet.unetspeedmeter.singleton.DailyDataSingleton
import kotlinx.coroutines.*
import java.util.*


class NotificationService : Service() {
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null


    private val dayChangedBroadcastReceiver = object : DayChangedBroadcastReceiver() {

        override fun onDayChanged(previousDate: String) {
            val obj = InternetDataItem(
                previousDate,
                DailyDataSingleton.mobileDataSend,
                DailyDataSingleton.mobileDataReceived,
                DailyDataSingleton.wifiDataSend,
                DailyDataSingleton.wifiDataReceived
            )

            CoroutineScope(Dispatchers.Default).launch {

                (applicationContext as UNetSpeedMeterApplication).appDataContainer
                    .itemsRepository.insertItem(
                        obj
                    )
            }

            with(DailyDataSingleton){
                mobileDataReceived = 0L
                mobileDataSend = 0L
                wifiDataReceived = 0L
                wifiDataSend = 0L
            }

        }

    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {


        @OptIn(DelicateCoroutinesApi::class)
        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message) {


            startForeground(
                SERVICE_NOTIFICATION_ID,
                showNotification(R.mipmap.ntp000000).build()
            )
            GlobalScope.launch {

                val mMath = Math(applicationContext)

                while (true) {
                    try {

                        mMath.main()
                        val totalSpeed = mMath.speedDownLoad + mMath.speedUpLoad
                        showNotification(
                            icon((totalSpeed / 1000).toInt())
                        )

                        val intent = Intent(CUSTOM_BROADCAST)
                        intent.putExtra(
                            "INTERNET_SEND",
                            kbToString(mMath.speedUpLoad)
                        )
                        intent.putExtra(
                            "INTERNET_RECEIVE",
                            kbToString(mMath.speedDownLoad)
                        )
                        sendBroadcast(intent)


                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }
            }

        }
    }


    @SuppressLint("MissingPermission")
    fun showNotification(ico: Int): NotificationCompat.Builder {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(ico)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(SERVICE_NOTIFICATION_ID, notification.build())
        }
        return notification
    }


    override fun onCreate() {

        registerReceiver(
            dayChangedBroadcastReceiver,
            DayChangedBroadcastReceiver.getIntentFilter()
        )

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_DEFAULT).apply {
            start()
            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
        createNotificationChannel()

    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        return START_NOT_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dayChangedBroadcastReceiver)

    }


    private fun createNotificationChannel() {

        val serviceChannel = NotificationChannel(
            CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)

    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }


}






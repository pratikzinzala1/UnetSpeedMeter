package com.internet.unetspeedmeter.service

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.internet.unetspeedmeter.*
import com.internet.unetspeedmeter.broadcast.DayChangedBroadcastReceiver
import com.internet.unetspeedmeter.data.InternetDataItem
import com.internet.unetspeedmeter.math.Math
import com.internet.unetspeedmeter.math.icon
import com.internet.unetspeedmeter.math.kbToString
import kotlinx.coroutines.*
import java.util.*


class NotificationService : Service() {
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null


    private val dayChangedBroadcastReceiver = object : DayChangedBroadcastReceiver() {

        override fun onDayChanged(previousDate: String) {

            Log.d("DDD","DATA ADDED")
            val obj = InternetDataItem(
                previousDate,
                applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                    SUM_MOBILE_UPLOAD, 0
                ),
                applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                    SUM_MOBILE_DOWNLOAD, 0
                ),
                applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                    SUM_WIFI_UPLOAD, 0
                ),
                applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                    SUM_WIFI_DOWNLOAD, 0
                )
            )

            CoroutineScope(Dispatchers.Default).launch {

                (applicationContext as UNetSpeedMeterApplication).appDataContainer
                    .itemsRepository.insertItem(
                        obj
                    )
            }

            applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit()
                .putLong(
                    SUM_WIFI_UPLOAD, 0
                ).apply()
            applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit()
                .putLong(
                    SUM_WIFI_DOWNLOAD, 0
                ).apply()
            applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit()
                .putLong(
                    SUM_MOBILE_DOWNLOAD, 0
                ).apply()
            applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit()
                .putLong(
                    SUM_MOBILE_UPLOAD, 0
                ).apply()

        }

    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        @OptIn(DelicateCoroutinesApi::class)
        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message) {

            Log.d("MSG", msg.arg1.toString())

            if (msg.arg1 == 1) {
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
                                "INTERNET_TOTAL",
                                kbToString(mMath.speedUpLoad + mMath.speedDownLoad)
                            )
                            sendBroadcast(intent)


                        } catch (e: Exception) {

                            e.printStackTrace()
                        }
                    }
                }

            }


        }
    }


    @SuppressLint("MissingPermission")
    fun showNotification(ico: Int): NotificationCompat.Builder {
        val mobile = kbToString(
            applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                SUM_MOBILE_UPLOAD, 0
            ) + applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                SUM_MOBILE_DOWNLOAD, 0
            )
        )
        val wifi = kbToString(
            applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                SUM_WIFI_UPLOAD, 0
            ) + applicationContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).getLong(
                SUM_WIFI_DOWNLOAD, 0
            )
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(
                "Mobile : $mobile Wifi : $wifi"
            )
            .setSmallIcon(ico)
            .setColor(ContextCompat.getColor(applicationContext,R.color.crane_purple_700))

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(SERVICE_NOTIFICATION_ID, notification.build())
        }
        return notification
    }


    override fun onCreate() {
        ContextCompat.registerReceiver(
            this,
            dayChangedBroadcastReceiver,
            DayChangedBroadcastReceiver.getIntentFilter(),
            ContextCompat.RECEIVER_EXPORTED
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

        return START_STICKY
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






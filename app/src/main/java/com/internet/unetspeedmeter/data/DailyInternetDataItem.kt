package com.internet.unetspeedmeter.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "DAILY_DATA")
data class DailyInternetDataItem(
    @PrimaryKey(autoGenerate = false)
    val date: String,
    val mobileData: String,
    val wifiData: String
)

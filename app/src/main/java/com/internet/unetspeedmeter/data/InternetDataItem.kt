package com.internet.unetspeedmeter.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "internet_data")
data class InternetDataItem(

    @PrimaryKey(autoGenerate = false)
    val time: String,
    val byteSendMobile: Long,
    val byteReceivedMobile: Long,
    val byteSendWifi: Long,
    val byteReceivedWifi: Long
)

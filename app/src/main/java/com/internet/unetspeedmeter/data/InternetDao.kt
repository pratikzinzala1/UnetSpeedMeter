package com.internet.unetspeedmeter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InternetItemDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: InternetDataItem)

    @Update
    suspend fun update(item: InternetDataItem)

    @Delete
    suspend fun delete(item: InternetDataItem)

    @Query("SELECT * from internet_data WHERE time = :time")
    fun getItem(time: Long): InternetDataItem

    @Query("SELECT * from internet_data ORDER BY time ")
    fun getAllItems(): List<InternetDataItem>


//    //Operation On Daily Data
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertDailyData(item: DailyInternetDataItem)
//
//    @Query("SELECT * from DAILY_DATA ORDER BY date")
//    fun getAllDailyItems(): List<DailyInternetDataItem>
//
//    @Delete
//    suspend fun deleteDailyData(item: DailyInternetDataItem)
//
//    @Update
//    suspend fun updateDailyData(item: DailyInternetDataItem)

}
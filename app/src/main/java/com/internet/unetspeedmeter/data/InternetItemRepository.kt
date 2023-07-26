package com.internet.unetspeedmeter.data


interface InternetItemRepository {

    fun getAllItemsStream(): List<InternetDataItem>


    fun getItemStream(time:Long): InternetDataItem


    suspend fun insertItem(item: InternetDataItem)


    suspend fun deleteItem(item: InternetDataItem)


    suspend fun updateItem(item: InternetDataItem)
}
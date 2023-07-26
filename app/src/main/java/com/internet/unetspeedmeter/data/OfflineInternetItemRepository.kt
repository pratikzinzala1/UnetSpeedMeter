package com.internet.unetspeedmeter.data


class OfflineInternetItemRepository(private val internetItemDao: InternetItemDao):InternetItemRepository {
    override fun getAllItemsStream():List<InternetDataItem> = internetItemDao.getAllItems()

    override fun getItemStream(time: Long): InternetDataItem = internetItemDao.getItem(time)

    override suspend fun insertItem(item: InternetDataItem) = internetItemDao.insert(item = item)

    override suspend fun deleteItem(item: InternetDataItem) = internetItemDao.delete(item = item)

    override suspend fun updateItem(item: InternetDataItem) =internetItemDao.update(item)

}
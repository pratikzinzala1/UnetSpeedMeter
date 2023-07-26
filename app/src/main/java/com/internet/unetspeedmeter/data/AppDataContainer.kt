package com.internet.unetspeedmeter.data

import android.content.Context

interface AppContainer {
    val itemsRepository: InternetItemRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val itemsRepository: InternetItemRepository by lazy {
        OfflineInternetItemRepository(InternetDatabase.getDatabase(context).internetItemDao())
    }

}
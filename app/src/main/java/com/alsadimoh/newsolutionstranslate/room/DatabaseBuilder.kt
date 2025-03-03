package com.alsadimoh.newsolutionstranslate.room

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.InternalCoroutinesApi

object DatabaseBuilder {
    private var INSTANCE: AppDatabase? = null


    @OptIn(InternalCoroutinesApi::class)
    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            kotlinx.coroutines.internal.synchronized(AppDatabase::class) {// synchronized عشان تجيب الاوبجكت من ثريد واحد .. يعني الكوروتين بتشتغل على اكتر من ثريد ولو ما عملناها سنكرونايز بترجع اكتر من اوبجكت واحنا محتاجين فقط اوبجكت واحد
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "jokes-app"
    ).build()
}
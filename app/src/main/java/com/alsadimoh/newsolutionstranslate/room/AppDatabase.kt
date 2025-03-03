package com.alsadimoh.newsolutionstranslate.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alsadimoh.newsolutionstranslate.room.dao.UserDao
import com.alsadimoh.newsolutionstranslate.common.FavoriteModel

@Database(entities = [FavoriteModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
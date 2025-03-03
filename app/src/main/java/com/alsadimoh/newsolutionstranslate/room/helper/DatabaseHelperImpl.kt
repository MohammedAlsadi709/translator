package com.alsadimoh.newsolutionstranslate.room.helper

import com.alsadimoh.newsolutionstranslate.common.FavoriteModel
import com.alsadimoh.newsolutionstranslate.room.AppDatabase

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    override suspend fun insertFavorite(favoriteModel: FavoriteModel): Long {
        return appDatabase.userDao().insertFavorite(favoriteModel)
    }

    override suspend fun getFavorites(): List<FavoriteModel> {
        return appDatabase.userDao().getFavorites()
    }

    override suspend fun deleteFavorite(id: Int): Int {
        return appDatabase.userDao().deleteFavorite(id)
    }

    override suspend fun deleteFavorite(
        sourceText: String,
        targetLang: String,
        sourceLang: String
    ): Int {
        return appDatabase.userDao().deleteFavorite(sourceText, targetLang, sourceLang)
    }

    override suspend fun getFavoriteIfExists(
        sourceLang: String,
        targetLang: String,
        sourceText: String
    ): List<FavoriteModel> {
        return appDatabase.userDao().getFavoriteIfExists(sourceLang, targetLang, sourceText)
    }

}
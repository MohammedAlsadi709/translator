package com.alsadimoh.newsolutionstranslate.room.helper

import com.alsadimoh.newsolutionstranslate.common.FavoriteModel


interface DatabaseHelper {
    suspend fun insertFavorite(favoriteModel: FavoriteModel): Long
    suspend fun getFavorites(): List<FavoriteModel>
    suspend fun deleteFavorite(id:Int): Int
    suspend fun deleteFavorite(sourceText:String,targetLang:String,sourceLang:String): Int
    suspend fun getFavoriteIfExists(sourceLang:String,targetLang:String,sourceText:String): List<FavoriteModel>

}
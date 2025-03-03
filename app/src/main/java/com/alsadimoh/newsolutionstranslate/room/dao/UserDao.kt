package com.alsadimoh.newsolutionstranslate.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alsadimoh.newsolutionstranslate.common.FavoriteModel


@Dao
interface UserDao {

    @Insert(entity = FavoriteModel::class)
    suspend fun insertFavorite(favoriteModel: FavoriteModel): Long

    @Query("Select * From Favorites Order by date desc")
    suspend fun getFavorites(): List<FavoriteModel>

    @Query("Delete From Favorites where id = :id")
    suspend fun deleteFavorite(id:Int): Int

    @Query("Delete From Favorites where sourceLang = :sourceLang and targetLang = :targetLang and sourceText = :sourceText")
    suspend fun deleteFavorite(sourceText:String,targetLang:String,sourceLang:String): Int

    @Query("Select * From Favorites where sourceLang = :sourceLang and targetLang = :targetLang and sourceText = :sourceText")
    suspend fun getFavoriteIfExists(sourceLang:String,targetLang:String,sourceText:String): List<FavoriteModel>

}
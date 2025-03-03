package com.alsadimoh.newsolutionstranslate.room.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alsadimoh.newsolutionstranslate.room.helper.DatabaseHelper
import com.alsadimoh.newsolutionstranslate.room.utils.Resource
import com.alsadimoh.newsolutionstranslate.common.FavoriteModel
import kotlinx.coroutines.launch

class RoomViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {

    private val favorites = MutableLiveData<Resource<List<FavoriteModel>>>()
    private val insertFavorite = MutableLiveData<Resource<Long>>()
    private val deleteFavorite = MutableLiveData<Resource<Int>>()
    private val deleteFavoriteByText = MutableLiveData<Resource<Int>>()
    private val checkFavoritesIfExists = MutableLiveData<Resource<List<FavoriteModel>>>()

    //=============================================================//
    // get favorites

    fun getFavorites() {
        viewModelScope.launch {
            favorites.postValue(Resource.loading(null))
            try {
                favorites.postValue(Resource.success(dbHelper.getFavorites()))
            } catch (e: Exception) {
                favorites.postValue(Resource.error("Something Error!", null))
            }
        }
    }

    fun getFavoritesResponse(): MutableLiveData<Resource<List<FavoriteModel>>> {
        return favorites
    }

    //=============================================================//
   // check favorites if exists

    fun checkFavoritesIfExists(sourceLang:String,targetLang:String,sourceText:String) {
        viewModelScope.launch {
            checkFavoritesIfExists.postValue(Resource.loading(null))
            try {
                checkFavoritesIfExists.postValue(Resource.success(dbHelper.getFavoriteIfExists(sourceLang, targetLang, sourceText)))
            } catch (e: Exception) {
                checkFavoritesIfExists.postValue(Resource.error("Something Error!", null))
            }
        }
    }

    fun checkFavoritesIfExistsResponse(): MutableLiveData<Resource<List<FavoriteModel>>> {
        return checkFavoritesIfExists
    }

    //=============================================================//
    // Insert favorite

    fun insertFavorite(favoriteModel: FavoriteModel) {
        viewModelScope.launch {
            insertFavorite.postValue(Resource.loading(null))
            try {
                insertFavorite.postValue(Resource.success(dbHelper.insertFavorite(favoriteModel)))
            } catch (e: Exception) {
                insertFavorite.postValue(Resource.error("Something Error!", null))
            }
        }
    }

    fun getInsertFavoriteStatus(): MutableLiveData<Resource<Long>> {
        return insertFavorite
    }

    //=============================================================//
    // delete favorite

    fun deleteFavorite(id: Int) {
        viewModelScope.launch {
            deleteFavorite.postValue(Resource.loading(null))
            try {
                deleteFavorite.postValue(Resource.success(dbHelper.deleteFavorite(id)))
            } catch (e: Exception) {
                deleteFavorite.postValue(Resource.error("Something Error!", null))
            }
        }
    }

    fun getDeleteFavoriteStatus(): MutableLiveData<Resource<Int>> {
        return deleteFavorite
    }
  //=============================================================//
    // delete favorite by text

    fun deleteFavorite(sourceText:String,targetLang:String,sourceLang:String) {
        viewModelScope.launch {
            deleteFavoriteByText.postValue(Resource.loading(null))
            try {
                deleteFavoriteByText.postValue(Resource.success(dbHelper.deleteFavorite(sourceText, targetLang, sourceLang)))
            } catch (e: Exception) {
                deleteFavoriteByText.postValue(Resource.error("Something Error!", null))
            }
        }
    }

    fun getDeleteFavoriteByTextStatus(): MutableLiveData<Resource<Int>> {
        return deleteFavoriteByText
    }

}
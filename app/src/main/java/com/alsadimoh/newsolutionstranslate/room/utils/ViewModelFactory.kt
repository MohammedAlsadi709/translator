package com.alsadimoh.newsolutionstranslate.room.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alsadimoh.newsolutionstranslate.room.helper.DatabaseHelper
import com.alsadimoh.newsolutionstranslate.room.viewModel.RoomViewModel

class ViewModelFactory(private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel( dbHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}
package com.alsadimoh.newsolutionstranslate.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favorites")
data class FavoriteModel(val sourceLang:String,val targetLang:String,val sourceText:String,val translatedText:String,val date:Long) {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}
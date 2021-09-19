package com.myvocab.myvocab.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "word_sets")
data class WordSetDbModel(

        @Json(name = "id")
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,

        @Json(name = "globalId")
        val globalId: String,

        @Json(name = "title")
        val title: String = ""

) : Serializable {

        companion object {
                const val MY_WORDS = "my_words"
        }
}
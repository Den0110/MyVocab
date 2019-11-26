package com.myvocab.myvocab.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "words")
data class Word(

        @Json(name = "id")
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,

        @Json(name = "word")
        val word: String?,

        @Json(name = "translation")
        var translation: String?,

        @Json(name = "knowingLevel")
        var knowingLevel: Int = 0,

        @Json(name = "lastShowTime")
        var lastShowTime: Long = 0

) : Serializable {
        constructor() : this(-1, "", "")
}
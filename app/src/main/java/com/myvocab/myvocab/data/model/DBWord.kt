package com.myvocab.myvocab.data.model

import androidx.room.*
import com.squareup.moshi.Json
import java.io.Serializable

@Entity(tableName = "words")
@TypeConverters(StringListConverter::class, ExampleListConverter::class)
data class DBWord(

        @Json(name = "id")
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,

        @Json(name = "word")
        val word: String? = "",

        @Json(name = "transcription")
        val transcription: String? = "",

        @Json(name = "meanings")
        val meanings: List<String>? = listOf(),

        @Json(name = "translation")
        val translation: String? = "",

        @Json(name = "synonyms")
        val synonyms: List<String>? = listOf(),

        @Json(name = "examples")
        val examples: List<Example>? = listOf(),

        @Json(name = "knowingLevel")
        val knowingLevel: Int = 0,

        @Json(name = "lastShowTime")
        val lastShowTime: Long = 0,

        @Json(name = "wordSetId")
        val wordSetId: String? = "",

        @Json(name = "needToLearn")
        val needToLearn: Boolean = true

) : Serializable {

    data class Example(
            val text: String,
            val translation: String
    ) : Serializable

}

class StringListConverter {

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(";")
    }

    @TypeConverter
    fun toList(data: String?): List<String> {
        if(data.isNullOrBlank()) return emptyList()
        return data.split(";").toTypedArray().toList()
    }
}
class ExampleListConverter {

    @TypeConverter
    fun fromExampleList(list: List<DBWord.Example>): String {
        return list.joinToString(";") {
            "${it.text},${it.translation}"
        }
    }

    @TypeConverter
    fun toExampleList(data: String?): List<DBWord.Example> {
        if(data.isNullOrBlank()) return emptyList()
        return data.split(";").toTypedArray().toList().mapNotNull {
            try {
                val values = it.split(",")
                DBWord.Example(values[0], values[1])
            } catch (e: Exception) {
                null
            }
        }
    }
}
package com.myvocab.domain.entities

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Word(
    val id: Int? = null,
    val word: String = "",
    val transcription: String = "",
    val meanings: List<String> = listOf(),
    val translation: String = "",
    val synonyms: List<String> = listOf(),
    val examples: List<Example> = listOf(),
    var knowingLevel: Int = 0,
    var lastShowTime: Long = 0,
    var wordSetId: String? = null,
    var needToLearn: Boolean = true
) : Parcelable, Serializable {

    companion object {
        val EMPTY = Word()
    }

    fun isEmpty() = this == EMPTY

    @Parcelize
    data class Example(
        val text: String = "",
        val translation: String = ""
    ) : Parcelable {

        companion object {
            private const val SEPARATOR = "*"

            fun getRawText(s: String) = s.replace(SEPARATOR, "")

            fun getHighlight(s: String): IntRange? {
                val start = s.indexOf(SEPARATOR)
                val end = s.indexOf(SEPARATOR, start + 1) - SEPARATOR.length * 2
                if (start <= -1 || end <= -1) {
                    return null
                }
                return IntRange(start, end)
            }

            fun setHighlight(s: String, start: Int, end: Int): String {
                return s.substring(0, start) + SEPARATOR + s.substring(
                    start,
                    end + 1
                ) + SEPARATOR + s.substring(end + 1)
            }
        }
    }
}

class WordDiffCallback : DiffUtil.ItemCallback<Word>() {

    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean = newItem.id == oldItem.id

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean = newItem == oldItem

}
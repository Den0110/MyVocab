package com.myvocab.myvocab.data.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.myvocab.myvocab.data.model.Word.Example.Companion.setHighlight
import com.myvocab.myvocab.util.Mapper
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

        const val MAX_MEANING_NUMBER = 4
        const val MAX_SYNONYMS_NUMBER = 4
        const val MAX_EXAMPLES_NUMBER = 5

        fun fromNetworkDictionaryModel() = object : Mapper<NetworkDictionaryModel, Word> {
            override fun map(input: NetworkDictionaryModel): Word {

                if(input.def.isNullOrEmpty() || input.def!![0].tr.isNullOrEmpty())
                    return EMPTY

                val entries = input.def!!
                val mainEntry = entries[0]
                val mainTranslation = mainEntry.tr[0]

                val meanings = mutableListOf<String>()
                val synonyms = mutableListOf<String>()
                val examples = mutableListOf<Example>()

                entries.forEach { entry ->
                    entry.tr.forEach { tr ->

                        if (meanings.size < MAX_MEANING_NUMBER) {
                            meanings.addAll(
                                    tr.mean
                                            ?.map { it.text }
                                            ?.take(MAX_MEANING_NUMBER - meanings.size)
                                            ?: listOf()
                            )
                        }
                        if (synonyms.size < MAX_SYNONYMS_NUMBER) {
                            if (tr.text != mainTranslation.text) {
                                synonyms.add(tr.text)
                            }
                            synonyms.addAll(
                                    tr.syn
                                            ?.map { it.text }
                                            ?.take(MAX_SYNONYMS_NUMBER - synonyms.size)
                                            ?: listOf()
                            )
                        }
                        if (examples.size < MAX_EXAMPLES_NUMBER) {
                            examples.addAll(
                                    tr.ex
                                            ?.mapNotNull {

                                                val text = it.text
                                                val translation = it.tr[0].text

                                                var textHighlightStart = -1
                                                var textHighlightEnd = 0-1 // inclusive
                                                var translationHighlightStart = -1
                                                var translationHighlightEnd = -1 // inclusive

                                                var iTmp: Int

                                                iTmp = text.indexOf(entry.text)
                                                if(iTmp > -1){
                                                    textHighlightStart = iTmp
                                                    textHighlightEnd = iTmp + entry.text.length - 1
                                                } else {
                                                    tr.mean?.forEach { mean ->
                                                        iTmp = text.indexOf(mean.text)
                                                        if (iTmp > -1) {
                                                            textHighlightStart = iTmp
                                                            textHighlightEnd = iTmp + mean.text.length - 1
                                                        }
                                                    }
                                                }

                                                iTmp = translation.indexOf(tr.text)
                                                if(iTmp > -1){
                                                    translationHighlightStart = iTmp
                                                    translationHighlightEnd = iTmp + tr.text.length - 1
                                                } else {
                                                    tr.syn?.forEach { syn ->
                                                        val i = translation.indexOf(syn.text)
                                                        if(i > -1){
                                                            translationHighlightStart = i
                                                            translationHighlightEnd = i + syn.text.length - 1
                                                        }
                                                    }
                                                }

                                                if(textHighlightStart > -1 && textHighlightEnd > -1 && translationHighlightStart > -1 && translationHighlightEnd > -1) {
                                                    Example(
                                                            setHighlight(text, textHighlightStart, textHighlightEnd),
                                                            setHighlight(translation, translationHighlightStart, translationHighlightEnd)
                                                    )
                                                } else null
                                            }
                                            ?.take(MAX_EXAMPLES_NUMBER - examples.size)
                                            ?: listOf()
                            )
                        }
                    }
                }

                return Word(
                        null,
                        mainEntry.text,
                        mainEntry.ts ?: "",
                        meanings,
                        mainTranslation.text,
                        synonyms,
                        examples
                )
            }
        }

        fun fromNetworkTranslatorModel() = object : Mapper<NetworkTranslatorModel, Word> {
            override fun map(input: NetworkTranslatorModel): Word {
                if(input.text.isNullOrEmpty())
                    return EMPTY
                return Word(
                        translation = input.text!![0],
                        synonyms = input.text?.subList(1, input.text?.size ?: 1) ?: listOf()
                )
            }
        }

        fun toDBWord() = object : Mapper<Word, DBWord> {
            override fun map(input: Word): DBWord {
                if(input.wordSetId == null)
                    throw Exception("WordSetId must be set!")
                return DBWord(
                        input.id,
                        input.word,
                        input.transcription,
                        input.meanings,
                        input.translation,
                        input.synonyms,
                        input.examples.map { DBWord.Example(it.text, it.translation) },
                        input.knowingLevel,
                        input.lastShowTime,
                        input.wordSetId!!,
                        input.needToLearn
                )
            }
        }

        fun fromDBWord() = object : Mapper<DBWord, Word> {
            override fun map(input: DBWord): Word {
                return Word(
                        id = input.id,
                        word = input.word ?: "",
                        transcription = input.transcription ?: "",
                        meanings = input.meanings ?: listOf(),
                        translation = input.translation ?: "",
                        synonyms = input.synonyms ?: listOf(),
                        examples = input.examples?.map { Example(it.text, it.translation) } ?: listOf(),
                        knowingLevel = input.knowingLevel,
                        lastShowTime = input.lastShowTime,
                        wordSetId = input.wordSetId,
                        needToLearn = input.needToLearn
                )
            }
        }

    }

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
                val end = s.indexOf(SEPARATOR, start+1) - SEPARATOR.length*2
                if(start <= -1 || end <= -1) {
                    return null
                }
                return IntRange(start, end)
            }

            fun setHighlight(s: String, start: Int, end: Int): String {
                return s.substring(0, start) + SEPARATOR + s.substring(start, end+1) + SEPARATOR + s.substring(end+1)
            }
        }
    }

    fun isEmpty() = this == EMPTY

}

class WordDiffCallback : DiffUtil.ItemCallback<Word>() {

    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean = newItem.id == oldItem.id

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean = newItem == oldItem

}
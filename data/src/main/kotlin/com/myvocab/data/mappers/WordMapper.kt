package com.myvocab.data.mappers

import com.myvocab.core.util.Mapper
import com.myvocab.data.model.DBWord
import com.myvocab.data.model.NetworkDictionaryModel
import com.myvocab.data.model.NetworkTranslatorModel
import com.myvocab.domain.entities.Word
import com.myvocab.domain.entities.Word.Example.Companion.setHighlight

const val MAX_MEANING_NUMBER = 4
const val MAX_SYNONYMS_NUMBER = 4
const val MAX_EXAMPLES_NUMBER = 5

fun fromNetworkDictionaryModel() = object : Mapper<NetworkDictionaryModel, Word> {
    override fun map(input: NetworkDictionaryModel): Word {

        if (input.def.isNullOrEmpty() || input.def!![0].tr.isNullOrEmpty())
            return Word.EMPTY

        val entries = input.def!!
        val mainEntry = entries[0]
        val mainTranslation = mainEntry.tr[0]

        val meanings = mutableListOf<String>()
        val synonyms = mutableListOf<String>()
        val examples = mutableListOf<Word.Example>()

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
                                var textHighlightEnd = 0 - 1 // inclusive
                                var translationHighlightStart = -1
                                var translationHighlightEnd = -1 // inclusive

                                var iTmp: Int

                                iTmp = text.indexOf(entry.text)
                                if (iTmp > -1) {
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
                                if (iTmp > -1) {
                                    translationHighlightStart = iTmp
                                    translationHighlightEnd = iTmp + tr.text.length - 1
                                } else {
                                    tr.syn?.forEach { syn ->
                                        val i = translation.indexOf(syn.text)
                                        if (i > -1) {
                                            translationHighlightStart = i
                                            translationHighlightEnd = i + syn.text.length - 1
                                        }
                                    }
                                }

                                if (textHighlightStart > -1 && textHighlightEnd > -1 && translationHighlightStart > -1 && translationHighlightEnd > -1) {
                                    Word.Example(
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
        if (input.text.isNullOrEmpty())
            return Word.EMPTY
        return Word(
            translation = input.text!![0],
            synonyms = input.text?.subList(1, input.text?.size ?: 1) ?: listOf()
        )
    }
}

fun toDBWord() = object : Mapper<Word, DBWord> {
    override fun map(input: Word): DBWord {
        if (input.wordSetId == null)
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
            examples = input.examples?.map { Word.Example(it.text, it.translation) } ?: listOf(),
            knowingLevel = input.knowingLevel,
            lastShowTime = input.lastShowTime,
            wordSetId = input.wordSetId,
            needToLearn = input.needToLearn
        )
    }
}

package com.myvocab.data.source.local

import com.myvocab.core.util.ListMapperImpl
import com.myvocab.data.mappers.fromDBWord
import com.myvocab.data.mappers.toDBWord
import com.myvocab.domain.entities.Word
import javax.inject.Inject

class WordLocalDataSource
@Inject
constructor(
    private val wordDao: WordDao
) {

    suspend fun getWordByContent(content: String): Word =
        fromDBWord().map(wordDao.getWordByContent(content))

    suspend fun getWordsByWordSetId(wordSetId: String): List<Word> =
        ListMapperImpl(fromDBWord()).map(wordDao.getWordsByWordSetId(wordSetId))

    suspend fun getInLearningWordsCount(): Int =
        wordDao.getInLearningWordsCount()

    suspend fun getWordsInLearningByKnowingLevel(knowingLevel: Int): List<Word> =
        ListMapperImpl(fromDBWord()).map(wordDao.getWordsInLearningByKnowingLevel(knowingLevel))

    suspend fun getWordsCountInWordSet(globalId: String): Int =
        wordDao.getWordsCountInWordSet(globalId)

    suspend fun getWordSetLearningPercentage(globalId: String): Int =
        wordDao.getLearningPercentageByWordSetId(globalId)

    suspend fun getWordById(id: Int): Word =
        fromDBWord().map(wordDao.getWordById(id))

    suspend fun addNewWord(word: Word) =
        wordDao.addNewWord(toDBWord().map(word))

    suspend fun addWords(words: List<Word>) =
        wordDao.addWords(ListMapperImpl(toDBWord()).map(words))

    suspend fun updateWord(word: Word) =
        wordDao.updateWord(toDBWord().map(word))

    suspend fun updateWords(words: List<Word>) =
        wordDao.updateWords(ListMapperImpl(toDBWord()).map(words))

    suspend fun deleteWord(word: Word) =
        wordDao.deleteWord(toDBWord().map(word))

    suspend fun deleteAllWords(): Int =
        wordDao.deleteAllWords()

    suspend fun deleteWordsByWordSetId(wordSetId: String) =
        wordDao.deleteWordsByWordSetId(wordSetId)

}
package com.myvocab.domain.repositories

import com.myvocab.domain.entities.Word

interface WordRepository {
    suspend fun getWordById(id: Int): Word
    suspend fun getWordByContent(content: String): Word
    suspend fun getWordsByWordSetId(wordSetId: String): List<Word>
    suspend fun getInLearningWordsByKnowingLevel(knowingLevel: Int): List<Word>
    suspend fun getInLearningWordsCount(): Int
    suspend fun getWordsCountInWordSet(globalId: String): Int
    suspend fun getWordSetLearningPercentage(globalId: String): Int
    suspend fun addMyWord(word: Word)
    suspend fun addNewWord(word: Word)
    suspend fun addWords(words: List<Word>)
    suspend fun updateWord(word: Word)
    suspend fun updateWords(words: List<Word>)
    suspend fun deleteWord(word: Word)
    suspend fun deleteWordsByWordSetId(wordSetId: String)
    suspend fun deleteAllWords(): Int
}
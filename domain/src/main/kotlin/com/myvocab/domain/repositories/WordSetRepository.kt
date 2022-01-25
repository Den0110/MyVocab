package com.myvocab.domain.repositories

import com.myvocab.core.util.RepositoryData
import com.myvocab.domain.entities.WordSet

interface WordSetRepository {
    suspend fun getAllWordSets(): List<WordSet>
    suspend fun getInLearningWordSets(): List<RepositoryData<WordSet>>
    suspend fun getLearnedWordSets(): List<RepositoryData<WordSet>>
    suspend fun isWordSetSavedLocally(globalId: String): Boolean
    suspend fun getWordSet(globalId: String): RepositoryData<WordSet>
    suspend fun addWordSet(wordSet: WordSet)
    suspend fun deleteWordSet(globalId: String)
    suspend fun deleteAllWordSets()
}
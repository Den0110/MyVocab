package com.myvocab.data.source

import com.myvocab.core.util.RepositoryData
import com.myvocab.data.source.local.WordSetLocalDataSource
import com.myvocab.data.source.remote.wordset.WordSetRemoteDataSource
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.repositories.WordSetRepository

class WordSetRepositoryImpl
constructor(
    private val wordSetLocalDataSource: WordSetLocalDataSource,
    private val wordSetRemoteDataSource: WordSetRemoteDataSource
) : WordSetRepository {

    override suspend fun getAllWordSets(): List<WordSet> {
        return wordSetRemoteDataSource.getAllWordSets()
    }

    override suspend fun getInLearningWordSets(): List<RepositoryData<WordSet>> {
        return wordSetLocalDataSource.getInLearningWordSets()
    }

    override suspend fun getLearnedWordSets(): List<RepositoryData<WordSet>> {
        return wordSetLocalDataSource.getLearnedWordSets()
    }

    override suspend fun isWordSetSavedLocally(globalId: String): Boolean {
        return wordSetLocalDataSource.getWordSetsCount(globalId) > 0
    }

    override suspend fun getWordSet(globalId: String): RepositoryData<WordSet> = try {
        wordSetLocalDataSource.getWordSet(globalId)
    } catch (e: Exception) {
        wordSetRemoteDataSource.getWordSet(globalId)
    }

    override suspend fun addWordSet(wordSet: WordSet) {
        wordSetLocalDataSource.addWordSet(wordSet)
    }

    override suspend fun deleteWordSet(globalId: String) {
        return wordSetLocalDataSource.deleteWordSet(globalId)
    }

    override suspend fun deleteAllWordSets() =
        wordSetLocalDataSource.deleteAllWordSets()
}
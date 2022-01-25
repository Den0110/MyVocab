package com.myvocab.data.source.local

import com.myvocab.core.util.ListMapperImpl
import com.myvocab.core.util.RepositoryData
import com.myvocab.core.util.Source
import com.myvocab.data.mappers.fromDBWord
import com.myvocab.data.model.WordSetDbModel
import com.myvocab.domain.entities.Word
import com.myvocab.domain.entities.WordSet
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class WordSetLocalDataSource
@Inject
constructor(
    private val wordDao: WordDao,
    private val wordSetDao: WordSetDao
) {

    suspend fun getInLearningWordSets(): List<RepositoryData<WordSet>> {
        return modelsToWordSets(wordSetDao.getInLearningWordSets())
    }

    suspend fun getLearnedWordSets(): List<RepositoryData<WordSet>> {
        return modelsToWordSets(wordSetDao.getLearnedWordSets())
    }

    suspend fun getWordSetsCount(globalId: String): Int {
        return wordSetDao.getWordSetsCount(globalId)
    }

    suspend fun getWordSet(globalId: String): RepositoryData<WordSet> = coroutineScope {
        val dbWordSet = async { getWordSetEntity(globalId) }
        val words = async { getWordsByWordSetId(globalId) }
        RepositoryData(
            WordSet(dbWordSet.await().globalId, dbWordSet.await().id, dbWordSet.await().title, words.await()),
            Source.LOCAL
        )
    }

    suspend fun addWordSet(wordSet: WordSet) {
        val model = WordSetDbModel(globalId = wordSet.globalId, title = wordSet.title)
        return wordSetDao.addWordSet(model)
    }

    suspend fun deleteWordSet(globalId: String) {
        return wordSetDao.deleteWordSet(globalId)
    }

    suspend fun deleteAllWordSets() =
        wordSetDao.deleteAll()

    private suspend fun getWordSetEntity(globalId: String): WordSetDbModel {
        return wordSetDao.getWordSetById(globalId)
    }

    private suspend fun getWordsByWordSetId(wordSetId: String): List<Word> {
        return ListMapperImpl(fromDBWord()).map(wordDao.getWordsByWordSetId(wordSetId))
    }

    private fun modelsToWordSets(models: List<WordSetDbModel>) =
        models.map { RepositoryData(WordSet(it.globalId, it.id, it.title), Source.LOCAL) }

}
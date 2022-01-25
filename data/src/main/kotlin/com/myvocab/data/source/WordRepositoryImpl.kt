package com.myvocab.data.source

import com.myvocab.data.model.WordSetDbModel
import com.myvocab.data.source.local.WordLocalDataSource
import com.myvocab.domain.entities.Word
import com.myvocab.domain.repositories.WordRepository

class WordRepositoryImpl
constructor(
    private val wordLocalDataSource: WordLocalDataSource
) : WordRepository {

    override suspend fun getWordById(id: Int): Word {
        return wordLocalDataSource.getWordById(id)
    }

    override suspend fun getWordByContent(content: String): Word {
        return wordLocalDataSource.getWordByContent(content)
    }

    override suspend fun getWordsByWordSetId(wordSetId: String): List<Word> {
        return wordLocalDataSource.getWordsByWordSetId(wordSetId)
    }

    override suspend fun getInLearningWordsByKnowingLevel(knowingLevel: Int): List<Word> {
        return wordLocalDataSource.getWordsInLearningByKnowingLevel(knowingLevel)
    }

    override suspend fun getInLearningWordsCount(): Int {
        return wordLocalDataSource.getInLearningWordsCount()
    }

    override suspend fun getWordsCountInWordSet(globalId: String): Int {
        return wordLocalDataSource.getWordsCountInWordSet(globalId)
    }

    override suspend fun getWordSetLearningPercentage(globalId: String): Int {
        return wordLocalDataSource.getWordSetLearningPercentage(globalId)
    }

    override suspend fun addMyWord(word: Word) {
        return addNewWord(word.apply { wordSetId = WordSetDbModel.MY_WORDS })
    }

    override suspend fun addNewWord(word: Word) {
        wordLocalDataSource.addNewWord(word)
    }

    override suspend fun addWords(words: List<Word>) {
        wordLocalDataSource.addWords(words)
    }

    override suspend fun updateWord(word: Word) {
        wordLocalDataSource.updateWord(word)
    }

    override suspend fun updateWords(words: List<Word>) {
        wordLocalDataSource.updateWords(words)
    }

    override suspend fun deleteWord(word: Word) {
        wordLocalDataSource.deleteWord(word)
    }

    override suspend fun deleteAllWords(): Int {
        return wordLocalDataSource.deleteAllWords()
    }

    override suspend fun deleteWordsByWordSetId(wordSetId: String) {
        wordLocalDataSource.deleteWordsByWordSetId(wordSetId)
    }

}
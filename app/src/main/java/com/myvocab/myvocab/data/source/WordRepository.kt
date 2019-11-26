package com.myvocab.myvocab.data.source

import android.util.Log
import com.myvocab.myvocab.data.source.local.WordsDao
import com.myvocab.myvocab.data.model.Word
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
class WordRepository
constructor(private val wordDao: WordsDao) {

    companion object {
        const val TAG = "WordRepository"
    }

//  To concat network and local db
//  return Observable.concatArrayEager(observableFromApi, observableFromDb)

    fun getWords(): Observable<List<Word>> {
        return getWordsFromDb().subscribeOn(Schedulers.io())
    }

    fun getInLearningWords(): Observable<List<Word>> {
        return getInLearningWordsWordsFromDb().subscribeOn(Schedulers.io())
    }

    fun getInLearningWordsCount(): Single<Int> {
        return getInLearningWordsCountFromDb().subscribeOn(Schedulers.io())
    }

    fun getLearnedWords(): Observable<List<Word>> {
        return getLearnedWordsFromDb().subscribeOn(Schedulers.io())
    }

    fun getWordsByKnowingLevel(knowingLevel: Int): Single<List<Word>> {
        return getWordByKnowingLevelFromDb(knowingLevel).subscribeOn(Schedulers.io())
    }

    fun getWordById(id: Int): Single<Word> {
        return getWordByIdFromDb(id).subscribeOn(Schedulers.io())
    }

    fun addWords(words: List<Word>): Completable {
        return addWordsToDb(words).subscribeOn(Schedulers.io())
    }

    fun addWord(word: Word): Completable {
        return addWordToDb(word).subscribeOn(Schedulers.io())
    }

    fun deleteWord(word: Word): Completable {
        return deleteWordFromDb(word).subscribeOn(Schedulers.io())
    }

    fun deleteAllWords(): Single<Int> {
        return deleteAllWordsFromDb().subscribeOn(Schedulers.io())
    }

    private fun getWordsFromDb(): Observable<List<Word>> =
            wordDao.getWords().toObservable().doOnNext { t -> Log.d(TAG, "Vocab size: $t.size") }

    private fun getInLearningWordsWordsFromDb(): Observable<List<Word>> =
            wordDao.getInLearningWords().toObservable().doOnNext { t -> Log.d(TAG, "Words in learning: $t.size") }

    private fun getInLearningWordsCountFromDb(): Single<Int> =
            wordDao.getInLearningWordsCount()

    private fun getLearnedWordsFromDb(): Observable<List<Word>> =
            wordDao.getLearnedWords().toObservable().doOnNext { t -> Log.d(TAG, "Learned words: $t.size") }

    private fun getWordByKnowingLevelFromDb(knowingLevel: Int): Single<List<Word>> =
            wordDao.getWordByKnowingLevel(knowingLevel)

    private fun getWordByIdFromDb(id: Int): Single<Word> =
            wordDao.getWordById(id)

    private fun addWordToDb(word: Word) =
            wordDao.addWord(word).doOnComplete { Log.e(TAG, "Word added: $word") }

    private fun addWordsToDb(words: List<Word>) =
            wordDao.addWords(words).doOnComplete { Log.e(TAG, "Words added: $words") }

    private fun deleteWordFromDb(word: Word) =
            wordDao.deleteWord(word).doOnComplete { Log.e(TAG, "Word deleted: $word") }

    private fun deleteAllWordsFromDb() =
            wordDao.deleteAllWords().doAfterSuccess { t -> Log.e(TAG, "Words deleted: $t") }

}
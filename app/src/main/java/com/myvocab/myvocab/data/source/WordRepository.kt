package com.myvocab.myvocab.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.myvocab.myvocab.data.source.local.WordDao
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.model.WordSetDbModel
import com.myvocab.myvocab.data.source.local.WordSetDao
import com.myvocab.myvocab.util.RepositoryData
import com.myvocab.myvocab.util.Source
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class WordRepository
constructor(
        private val wordDao: WordDao,
        private val wordSetDao: WordSetDao
) {

    companion object {
        private const val TAG = "WordRepository"
    }

    //  To concat network and local db
    //  return Observable.concatArrayEager(observableFromApi, observableFromDb)

    fun getWordSets(): Observable<List<WordSet>> {
        return wordSetDao
                .getWordSets()
                .toObservable()
                .map { modelsToWordSets(it) }
                .subscribeOn(Schedulers.io())
    }

    fun getInLearningWordSets(): Observable<List<WordSet>> {
        return wordSetDao
                .getInLearningWordSets()
                .toObservable()
                .map { modelsToWordSets(it) }
                .subscribeOn(Schedulers.io())
    }

    fun getLearnedWordSets(): Observable<List<WordSet>> {
        return wordSetDao
                .getLearnedWordSets()
                .toObservable()
                .map { modelsToWordSets(it) }
                .subscribeOn(Schedulers.io())
    }

    fun wordSetSavedLocally(globalId: String): Single<Boolean> {
        return wordSetDao
                .isWordSetExist(globalId)
                .map { it > 0 }
                .subscribeOn(Schedulers.io())
    }

    private fun modelsToWordSets(models: List<WordSetDbModel>) =
            models.map { WordSet(it.globalId, it.id, it.title) }

    fun getWordSet(globalId: String): Single<RepositoryData<WordSet>> =
            wordSetDao
                    .getWordSetById(globalId)
                    .zipWith(wordDao.getWordsByWordSetId(globalId), BiFunction { model: WordSetDbModel, words: List<Word> ->
                        RepositoryData(WordSet(model.globalId, model.id, model.title, words), Source.LOCAL)
                    })
                    .onErrorResumeNext {
                        RxFirestore
                                .getDocument(FirebaseFirestore.getInstance().collection("word_sets").document(globalId))
                                .map {
                                    val obj = it.toObject(WordSet::class.java)!!
                                    val wordSet = WordSet(it.id, null, obj.title, obj.words)
                                    RepositoryData(wordSet, Source.REMOTE)
                                }
                                .toSingle()
                    }
                    .subscribeOn(Schedulers.io())

    fun getWordSetLearningPercentage(globalId: String) =
            wordDao
                    .getLearningPercentageByWordSetId(globalId)
                    .subscribeOn(Schedulers.io())

    fun getWordSetFromDb(globalId: String): Single<WordSet> =
            wordSetDao
                    .getWordSetById(globalId)
                    .zipWith(wordDao.getWordsByWordSetId(globalId), BiFunction { model: WordSetDbModel, words: List<Word> ->
                        WordSet(model.globalId, model.id, model.title, words)
                    })
                    .subscribeOn(Schedulers.io())

    fun addWordSet(wordSet: WordSet): Completable {
        val model = WordSetDbModel(globalId = wordSet.globalId, title = wordSet.title)
        return wordSetDao
                .addWordSet(model)
                .andThen(Completable.defer {
                    for (w: Word in wordSet.words)
                        w.wordSetId = wordSet.globalId
                    addWords(wordSet.words)
                })
                .subscribeOn(Schedulers.io())
    }

    fun deleteWordSet(globalId: String): Completable {
        return wordSetDao
                .deleteWordSet(globalId)
                .andThen(Completable.defer { wordDao.deleteWordsByWordSetId(globalId) })
                .subscribeOn(Schedulers.io())
    }

    fun deleteAllWordSets(): Completable =
        wordSetDao
                .deleteAll()
                .subscribeOn(Schedulers.io())

    fun getInLearningWordsCount(): Single<Int> {
        return getInLearningWordsCountFromDb().subscribeOn(Schedulers.io())
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

    fun addMyWord(word: Word): Completable {
        return addWord(word.apply { wordSetId = "my_words" })
    }

    fun deleteWord(word: Word): Completable {
        return deleteWordFromDb(word).subscribeOn(Schedulers.io())
    }

    fun deleteAllWords(): Single<Int> {
        return deleteAllWordsFromDb().subscribeOn(Schedulers.io())
    }

    private fun getInLearningWordsCountFromDb(): Single<Int> =
            wordDao.getInLearningWordsCount()

    private fun getWordByKnowingLevelFromDb(knowingLevel: Int): Single<List<Word>> =
            wordDao.getWordByKnowingLevel(knowingLevel)

    private fun getWordByIdFromDb(id: Int): Single<Word> =
            wordDao.getWordById(id)

    private fun addWordToDb(word: Word) =
            wordDao.addWord(word)

    private fun addWordsToDb(words: List<Word>) =
            wordDao.addWords(words)

    private fun deleteWordFromDb(word: Word) =
            wordDao.deleteWord(word)

    private fun deleteAllWordsFromDb() =
            wordDao.deleteAllWords()

}
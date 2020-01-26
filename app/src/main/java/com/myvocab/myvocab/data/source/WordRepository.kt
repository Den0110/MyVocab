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
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class WordRepository
constructor(
        private val wordDao: WordDao,
        private val wordSetDao: WordSetDao
) {

    //  To concat network and local db
    //  return Observable.concatArrayEager(observableFromApi, observableFromDb)

    /**
     *
     * WORD SET
     *
     */

    fun getWordSets(): Single<List<RepositoryData<WordSet>>> {
        return wordSetDao
                .getWordSets()
                .map { modelsToWordSets(it) }
                .subscribeOn(Schedulers.io())
    }

    fun getInLearningWordSets(): Single<List<RepositoryData<WordSet>>> {
        return wordSetDao
                .getInLearningWordSets()
                .map { modelsToWordSets(it) }
                .subscribeOn(Schedulers.io())
    }

    fun getLearnedWordSets(): Single<List<RepositoryData<WordSet>>> {
        return wordSetDao
                .getLearnedWordSets()
                .map { modelsToWordSets(it) }
                .subscribeOn(Schedulers.io())
    }

    fun isWordSetSavedLocally(globalId: String): Single<Boolean> {
        return wordSetDao
                .getWordSetsCount(globalId)
                .map { it > 0 }
                .subscribeOn(Schedulers.io())
    }

    fun getWordsCountInWordSet(globalId: String): Single<Int> {
        return wordDao
                .getWordsCountInWordSet(globalId)
                .subscribeOn(Schedulers.io())
    }

    private fun modelsToWordSets(models: List<WordSetDbModel>) =
            models.map { RepositoryData(WordSet(it.globalId, it.id, it.title), Source.LOCAL) }

    fun getWordSet(globalId: String): Single<RepositoryData<WordSet>> =
            getWordSetFromDb(globalId)
                    .onErrorResumeNext { getWordSetFromServer(globalId) }
                    .subscribeOn(Schedulers.io())

    private fun getWordSetFromDb(globalId: String): Single<RepositoryData<WordSet>> =
            getWordSetEntityFromDb(globalId)
                    .zipWith(getWordsByWordSetId(globalId), BiFunction { model: WordSetDbModel, words: List<Word> ->
                        RepositoryData(WordSet(model.globalId, model.id, model.title, words), Source.LOCAL)
                    })
                    .subscribeOn(Schedulers.io())

    private fun getWordSetFromServer(globalId: String): Single<RepositoryData<WordSet>> =
            RxFirestore
                    .getDocument(FirebaseFirestore.getInstance().collection("word_sets").document(globalId))
                    .map {
                        val obj = it.toObject(WordSet::class.java)!!
                        val wordSet = WordSet(it.id, null, obj.title, obj.words)
                        RepositoryData(wordSet, Source.REMOTE)
                    }
                    .toSingle()

    fun getWordSetLearningPercentage(globalId: String) =
            wordDao
                    .getLearningPercentageByWordSetId(globalId)
                    .subscribeOn(Schedulers.io())

    fun getWordSetEntityFromDb(globalId: String) =
            wordSetDao.getWordSetById(globalId)

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


    /**
     *
     * WORD
     *
     */

    fun getWordById(id: Int): Single<Word> {
        return getWordByIdFromDb(id).subscribeOn(Schedulers.io())
    }

    fun getWordByContent(content: String): Single<Word> {
        return wordDao.getWordByContent(content).subscribeOn(Schedulers.io())
    }

    fun getWordsByWordSetId(wordSetId: String): Single<List<Word>> {
        return wordDao.getWordsByWordSetId(wordSetId).subscribeOn(Schedulers.io())
    }

    fun getInLearningWordsByKnowingLevel(knowingLevel: Int): Single<List<Word>> {
        return getWordsInLearningByKnowingLevelFromDb(knowingLevel).subscribeOn(Schedulers.io())
    }

    fun getInLearningWordsCount(): Single<Int> {
        return getInLearningWordsCountFromDb().subscribeOn(Schedulers.io())
    }

    fun addMyWord(word: Word): Completable {
        return addWord(word.apply { wordSetId = "my_words" })
    }

    fun addWord(word: Word): Completable {
        return addWordToDb(word).subscribeOn(Schedulers.io())
    }

    fun addWords(words: List<Word>): Completable {
        return addWordsToDb(words).subscribeOn(Schedulers.io())
    }

    fun updateWord(word: Word): Completable {
        return updateWordInDb(word).subscribeOn(Schedulers.io())
    }

    fun deleteWord(word: Word): Completable {
        return deleteWordFromDb(word).subscribeOn(Schedulers.io())
    }

    fun deleteAllWords(): Single<Int> {
        return deleteAllWordsFromDb().subscribeOn(Schedulers.io())
    }

    private fun getInLearningWordsCountFromDb(): Single<Int> =
            wordDao.getInLearningWordsCount()

    private fun getWordsInLearningByKnowingLevelFromDb(knowingLevel: Int): Single<List<Word>> =
            wordDao.getWordsInLearningByKnowingLevel(knowingLevel)

    private fun getWordByIdFromDb(id: Int): Single<Word> =
            wordDao.getWordById(id)

    private fun addWordToDb(word: Word) =
            wordDao.addWord(word)

    private fun addWordsToDb(words: List<Word>) =
            wordDao.addWords(words)

    private fun updateWordInDb(word: Word) =
            wordDao.updateWord(word)

    private fun deleteWordFromDb(word: Word) =
            wordDao.deleteWord(word)

    private fun deleteAllWordsFromDb() =
            wordDao.deleteAllWords()

}
package com.myvocab.myvocab.data.source.local

import androidx.room.*
import com.myvocab.myvocab.data.model.Word
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface WordsDao {

    @Query("SELECT * FROM words ORDER BY id DESC")
    fun getWords(): Flowable<List<Word>>

    @Query("SELECT * FROM words WHERE knowingLevel < 3 ORDER BY id DESC")
    fun getInLearningWords(): Flowable<List<Word>>

    @Query("SELECT COUNT(id) FROM words WHERE knowingLevel < 3")
    fun getInLearningWordsCount(): Single<Int>

    @Query("SELECT * FROM words WHERE knowingLevel >= 3 ORDER BY id DESC")
    fun getLearnedWords(): Flowable<List<Word>>

    @Query("SELECT * FROM words WHERE knowingLevel = :knowingLevel ORDER BY lastShowTime ASC")
    fun getWordByKnowingLevel(knowingLevel: Int): Single<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    fun getWordById(id: Int): Single<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWord(word: Word): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWords(words: List<Word>): Completable

    @Delete
    fun deleteWord(word: Word): Completable

    @Query("DELETE FROM words")
    fun deleteAllWords(): Single<Int>

}
package com.myvocab.myvocab.data.source.local

import androidx.room.*
import com.myvocab.myvocab.data.model.Word
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY id DESC")
    fun getWords(): Flowable<List<Word>>

    @Query("SELECT * FROM words WHERE wordSetId = :globalId")
    fun getWordsByWordSetId(globalId: String): Single<List<Word>>

    @Query("SELECT COUNT(*) FROM words WHERE wordSetId = :globalId")
    fun getWordsCountInWordSet(globalId: String): Single<Int>

    @Query("SELECT (SUM(knowingLevel) + 0.0)/COUNT(knowingLevel)/3.0*100.0 FROM words WHERE wordSetId = :globalId")
    fun getLearningPercentageByWordSetId(globalId: String): Single<Int>

    @Query("SELECT * FROM words WHERE knowingLevel < 3 ORDER BY id DESC")
    fun getInLearningWords(): Flowable<List<Word>>

    @Query("SELECT COUNT(id) FROM words WHERE knowingLevel < 3 and needToLearn = 1")
    fun getInLearningWordsCount(): Single<Int>

    @Query("SELECT * FROM words WHERE knowingLevel = :knowingLevel and needToLearn = 1 ORDER BY lastShowTime ASC")
    fun getWordsInLearningByKnowingLevel(knowingLevel: Int): Single<List<Word>>

    @Query("SELECT * FROM words WHERE knowingLevel >= 3 ORDER BY id DESC")
    fun getLearnedWords(): Flowable<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    fun getWordById(id: Int): Single<Word>

    @Query("SELECT * FROM words WHERE word = :content LIMIT 1")
    fun getWordByContent(content: String): Single<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWord(word: Word): Completable

    @Update
    fun updateWord(word: Word): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWords(words: List<Word>): Completable

    @Delete
    fun deleteWord(word: Word): Completable

    @Query("DELETE FROM words")
    fun deleteAllWords(): Single<Int>

    @Query("DELETE FROM words WHERE wordSetId = :globalId")
    fun deleteWordsByWordSetId(globalId: String): Completable

}
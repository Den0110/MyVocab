package com.myvocab.myvocab.data.source.local

import androidx.room.*
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.model.WordSetDbModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface WordSetDao {

    @Query("SELECT * FROM word_sets ORDER BY id DESC")
    fun getWordSets(): Flowable<List<WordSetDbModel>>

    @Query("""
        SELECT ws.id, ws.globalId, ws.title 
        FROM word_sets ws 
        LEFT OUTER JOIN words w ON (ws.globalId = w.wordSetId) 
        WHERE w.knowingLevel < 3 and ws.globalId != "my_words"
        GROUP BY ws.globalId"""
    )
    fun getInLearningWordSets(): Flowable<List<WordSetDbModel>>

    @Query("""
        SELECT ws.id, ws.globalId, ws.title 
        FROM word_sets ws 
        LEFT OUTER JOIN words w 
        ON (ws.globalId = w.wordSetId)
        WHERE ws.globalId != "my_words"
        GROUP BY ws.globalId 
        HAVING COUNT(w.knowingLevel >= 3 OR NULL) = COUNT(*)"""
    )
    fun getLearnedWordSets(): Flowable<List<WordSetDbModel>>

    @Query("SELECT COUNT(*) FROM word_sets WHERE globalId = :globalId")
    fun isWordSetExist(globalId: String): Single<Int>

    @Query("SELECT * FROM word_sets WHERE globalId = :globalId LIMIT 1")
    fun getWordSetById(globalId: String): Single<WordSetDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWordSet(wordSet: WordSetDbModel): Completable

    @Query("DELETE FROM word_sets WHERE globalId = :globalId")
    fun deleteWordSet(globalId: String): Completable

    @Query("DELETE FROM word_sets")
    fun deleteAll(): Completable

}
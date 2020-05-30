package com.myvocab.myvocab.data.source.local

import androidx.room.*
import com.myvocab.myvocab.data.model.WordSetDbModel
import com.myvocab.myvocab.data.model.WordSetDbModel.Companion.MY_WORDS
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WordSetDao {

    @Query("SELECT * FROM word_sets ORDER BY id DESC")
    fun getWordSets(): Single<List<WordSetDbModel>>

//    @Query("""
//        SELECT ws.id, ws.globalId, ws.title
//        FROM word_sets ws
//        LEFT OUTER JOIN words w
//        ON (ws.globalId = w.wordSetId)
//        WHERE ws.globalId != "$MY_WORDS"
//        GROUP BY ws.globalId
//        HAVING COUNT(w.knowingLevel >= 3 OR NULL) < COUNT(w.needToLearn = 1 OR NULL)"""
//    )
    @Query("""
        SELECT *
        FROM word_sets ws
        WHERE (
            (
                SELECT COUNT(*)
                FROM words w 
                WHERE w.wordSetId = ws.globalId AND w.needToLearn = 1 AND w.knowingLevel >= 3
            ) < (
                SELECT COUNT(*) 
                FROM words w 
                WHERE w.wordSetId = ws.globalId AND w.needToLearn = 1
            )
            OR (
                SELECT COUNT(*)
                FROM words w 
                WHERE w.wordSetId = ws.globalId AND w.needToLearn = 1 AND w.knowingLevel >= 3
            ) = 0
        ) AND ws.globalId != "$MY_WORDS"
    """)
    fun getInLearningWordSets(): Single<List<WordSetDbModel>>

//    @Query("""
//        SELECT ws.id, ws.globalId, ws.title
//        FROM word_sets ws
//        LEFT OUTER JOIN words w
//        ON (ws.globalId = w.wordSetId)
//        WHERE ws.globalId != "$MY_WORDS"
//        GROUP BY ws.globalId
//        HAVING COUNT(w.knowingLevel >= 3 OR NULL) >= COUNT(w.needToLearn = 1 OR NULL)"""
//    )
    @Query("""
        SELECT *
        FROM word_sets ws
        WHERE (
            (
                SELECT COUNT(*)
                FROM words w
                WHERE w.wordSetId = ws.globalId AND w.needToLearn = 1 AND w.knowingLevel >= 3
            ) >= (
                SELECT COUNT(*)
                FROM words w
                WHERE w.wordSetId = ws.globalId AND w.needToLearn = 1
            )
            AND (
                SELECT COUNT(*)
                FROM words w 
                WHERE w.wordSetId = ws.globalId AND w.needToLearn = 1 AND w.knowingLevel >= 3
            ) > 0
        ) AND ws.globalId != "$MY_WORDS"
    """)
    fun getLearnedWordSets(): Single<List<WordSetDbModel>>

    @Query("SELECT COUNT(*) FROM word_sets WHERE globalId = :globalId")
    fun getWordSetsCount(globalId: String): Single<Int>

    @Query("SELECT * FROM word_sets WHERE globalId = :globalId LIMIT 1")
    fun getWordSetById(globalId: String): Single<WordSetDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWordSet(wordSet: WordSetDbModel): Completable

    @Query("DELETE FROM word_sets WHERE globalId = :globalId")
    fun deleteWordSet(globalId: String): Completable

    @Query("DELETE FROM word_sets")
    fun deleteAll(): Completable

}
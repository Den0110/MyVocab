package com.myvocab.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myvocab.data.model.WordSetDbModel
import com.myvocab.data.model.WordSetDbModel.Companion.MY_WORDS

@Dao
interface WordSetDao {

    @Query("SELECT * FROM word_sets ORDER BY id DESC")
    suspend fun getWordSets(): List<WordSetDbModel>

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
    suspend fun getInLearningWordSets(): List<WordSetDbModel>

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
    suspend fun getLearnedWordSets(): List<WordSetDbModel>

    @Query("SELECT COUNT(*) FROM word_sets WHERE globalId = :globalId")
    suspend fun getWordSetsCount(globalId: String): Int

    @Query("SELECT * FROM word_sets WHERE globalId = :globalId LIMIT 1")
    suspend fun getWordSetById(globalId: String): WordSetDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWordSet(wordSet: WordSetDbModel)

    @Query("DELETE FROM word_sets WHERE globalId = :globalId")
    suspend fun deleteWordSet(globalId: String)

    @Query("DELETE FROM word_sets")
    suspend fun deleteAll()

}
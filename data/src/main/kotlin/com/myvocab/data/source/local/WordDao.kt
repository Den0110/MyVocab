package com.myvocab.data.source.local

import androidx.room.*
import com.myvocab.data.model.DBWord

@Dao
interface WordDao {

//    @Query("SELECT * FROM words ORDER BY id DESC")
//    fun getWords(): Flowable<List<DBWord>>

    @Query("SELECT * FROM words WHERE wordSetId = :globalId")
    suspend fun getWordsByWordSetId(globalId: String): List<DBWord>

    @Query("SELECT COUNT(*) FROM words WHERE wordSetId = :globalId")
    suspend fun getWordsCountInWordSet(globalId: String): Int

    @Query("SELECT (SUM(knowingLevel) + 0.0)/COUNT(knowingLevel)/3.0*100.0 FROM words WHERE wordSetId = :globalId")
    suspend fun getLearningPercentageByWordSetId(globalId: String): Int

//    @Query("SELECT * FROM words WHERE knowingLevel < 3 ORDER BY id DESC")
//    fun getInLearningWords(): Flowable<List<DBWord>>

    @Query("SELECT COUNT(id) FROM words WHERE knowingLevel < 3 and needToLearn = 1")
    suspend fun getInLearningWordsCount(): Int

    @Query("SELECT * FROM words WHERE knowingLevel = :knowingLevel and needToLearn = 1 ORDER BY lastShowTime ASC")
    suspend fun getWordsInLearningByKnowingLevel(knowingLevel: Int): List<DBWord>

//    @Query("SELECT * FROM words WHERE knowingLevel >= 3 ORDER BY id DESC")
//    fun getLearnedWords(): Flowable<List<DBWord>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Int): DBWord

    @Query("SELECT * FROM words WHERE word = :content LIMIT 1")
    suspend fun getWordByContent(content: String): DBWord

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addNewWord(word: DBWord)

    @Update
    suspend fun updateWord(word: DBWord)

    @Update
    suspend fun updateWords(words: List<DBWord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWords(words: List<DBWord>)

    @Delete
    suspend fun deleteWord(word: DBWord)

    @Query("DELETE FROM words")
    suspend fun deleteAllWords(): Int

    @Query("DELETE FROM words WHERE wordSetId = :globalId")
    suspend fun deleteWordsByWordSetId(globalId: String)

}
package com.myvocab.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myvocab.data.model.DBWord
import com.myvocab.data.model.WordSetDbModel

@Database(entities = [DBWord::class, WordSetDbModel::class], version = 5)
abstract class Database : RoomDatabase() {

    companion object {

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN transcription TEXT")
                database.execSQL("ALTER TABLE words ADD COLUMN meanings TEXT")
                database.execSQL("ALTER TABLE words ADD COLUMN synonyms TEXT")
                database.execSQL("ALTER TABLE words ADD COLUMN examples TEXT")
            }
        }
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX index_unique_word ON words(word, translation)")
            }
        }
    }

    abstract fun wordsDao(): WordDao
    abstract fun wordSetsDao(): WordSetDao
}
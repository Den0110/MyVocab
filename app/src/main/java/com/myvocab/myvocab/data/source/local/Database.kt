package com.myvocab.myvocab.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myvocab.myvocab.data.model.Word

@Database(entities = [Word::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun wordsDao(): WordsDao
}
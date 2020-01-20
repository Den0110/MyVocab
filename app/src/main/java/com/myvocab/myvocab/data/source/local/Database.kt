package com.myvocab.myvocab.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.model.WordSetDbModel

@Database(entities = [Word::class, WordSetDbModel::class], version = 3)
abstract class Database : RoomDatabase() {
    abstract fun wordsDao(): WordDao
    abstract fun wordSetsDao(): WordSetDao
}
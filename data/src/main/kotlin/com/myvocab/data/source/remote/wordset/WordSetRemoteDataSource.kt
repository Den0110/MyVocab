package com.myvocab.data.source.remote.wordset

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.myvocab.core.util.RepositoryData
import com.myvocab.core.util.Source
import com.myvocab.domain.entities.WordSet
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WordSetRemoteDataSource
@Inject
constructor() {

    suspend fun getAllWordSets(): List<WordSet> {
        val collection = FirebaseFirestore.getInstance()
            .collection("word_sets")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        return collection.documents.map { doc ->
            val obj = doc.toObject(WordSet::class.java)!!
            WordSet(doc.id, null, obj.title, obj.words.reversed())
        }
    }

    suspend fun getWordSet(globalId: String): RepositoryData<WordSet> {
        val doc = FirebaseFirestore.getInstance().collection("word_sets").document(globalId).get().await()
        val obj = doc.toObject(WordSet::class.java)!!
        val wordSet = WordSet(doc.id, null, obj.title, obj.words)
        return RepositoryData(wordSet, Source.REMOTE)
    }

}
package com.myvocab.myvocab.domain.search

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.WordSetUseCase
import com.myvocab.myvocab.util.RepositoryData
import com.myvocab.myvocab.util.Source
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Single
import javax.inject.Inject

class GetSearchWordSetsUseCase
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val wordSetUseCase: WordSetUseCase
) {

    fun getWordSets(): Single<List<WordSetUseCaseResult>> =
            RxFirestore
                    .getCollection(FirebaseFirestore.getInstance().collection("word_sets"))
                    .map {
                        val wordSetResults = mutableListOf<WordSet>()
                        for (doc: DocumentSnapshot in it.documents) {
                            val obj = doc.toObject(WordSet::class.java)!!
                            val wordSet = WordSet(doc.id, null, obj.title, obj.words.reversed())
                            wordSetResults.add(wordSet)
                        }
                        wordSetResults.toList()
                    }
                    .toObservable()
                    .flatMapIterable { it }
                    .flatMapSingle { wordSet ->
                        wordRepository
                                .isWordSetSavedLocally(wordSet.globalId)
                                .map {
                                    if(it){
                                        RepositoryData(wordSet, Source.LOCAL)
                                    } else {
                                        RepositoryData(wordSet, Source.REMOTE)
                                    }
                                }
                    }
                    .flatMapSingle {
                        wordSetUseCase.execute(it)
                    }
                    .toList()

}
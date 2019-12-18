package com.myvocab.myvocab.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Resource
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        context: Application
) : AndroidViewModel(context) {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    val wordSets: MutableLiveData<Resource<List<WordSet>>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    init {
        loadWordSets()
    }

    fun loadWordSets(){
        val wordSetsDisposable = RxFirestore
                .getCollection(FirebaseFirestore.getInstance().collection("word_sets"))
                .map {
                    val wordSets = mutableListOf<WordSet>()
                    for (doc: DocumentSnapshot in it.documents){
                        val obj = doc.toObject(WordSet::class.java)!!
                        val wordSet = WordSet(doc.id, null, obj.title, obj.words.reversed())
                        wordSets.add(wordSet)
                    }
                    wordSets
                }
                .toObservable()
                .publish {
                    it
                            .timeout(500, TimeUnit.MILLISECONDS, Observable.create<MutableList<WordSet>>{
                                wordSets.postValue(Resource.loading())
                            })
                            .mergeWith(it)
                }
                .subscribe({
                    wordSets.postValue(Resource.success(it))
                },{
                    wordSets.postValue(Resource.error(it))
                })
        compositeDisposable.clear()
        compositeDisposable.add(wordSetsDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}
package com.myvocab.myvocab.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Resource
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SearchViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        context: Application
) : AndroidViewModel(context) {

    companion object {
        private val TAG = "SearchViewModel"
    }

    val wordSets: MutableLiveData<Resource<List<WordSet>>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    init {
        loadWordSets()
    }

    fun loadWordSets(){
        val wordSetsDisposable = RxFirestore
                .getCollection(FirebaseFirestore.getInstance().collection("word_sets"))
                .doOnSubscribe { wordSets.value = Resource.loading() }
                .map {
                    val wordSets = mutableListOf<WordSet>()
                    for (doc: DocumentSnapshot in it.documents){
                        val wordSet = doc.toObject(WordSet::class.java)!!
                        wordSets.add(WordSet(doc.id, wordSet.title, wordSet.words.reversed()))
                    }
                    wordSets
                }
                .subscribe({
                    wordSets.postValue(Resource.success(it))
                },{
                    wordSets.postValue(Resource.error(it))
                })
        compositeDisposable.clear()
        compositeDisposable.add(wordSetsDisposable)
    }

    fun addWords(words: List<Word>) =
            wordRepository.addWords(words)
                    .observeOn(AndroidSchedulers.mainThread())

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}
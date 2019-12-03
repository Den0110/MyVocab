package com.myvocab.myvocab.ui.add_new_word

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Resource
import com.opencsv.CSVParserBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import com.opencsv.CSVReaderBuilder
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.net.URI
import javax.inject.Inject


class AddNewWordViewModel
@Inject
constructor(private val wordRepository: WordRepository) : ViewModel() {

    companion object {
        private val TAG = "AddNewWordViewModel"

        val TEST_WORDS: List<Word> = listOf(
                Word(null, "addicted", "зависимый"),
                Word(null, "appearance", "внешний вид"),
                Word(null, "especially", "особенно"),
                Word(null, "brakes", "тормоза"),
                Word(null, "cosy", "уютный"),
                Word(null, "dedicated", "преданный"),
                Word(null, "drown", "тонуть"),
                Word(null, "exaggerate", "преувеличивать"),
                Word(null, "exterior", "сторонний"),
                Word(null, "handsome", "симпатичный"),
                Word(null, "promise", "обещать")
        )
    }

    val newWord: MutableLiveData<String> = MutableLiveData()
    val translation: MutableLiveData<String> = MutableLiveData()

    fun addWord() =
        wordRepository
                .addWord(Word(word = newWord.value, translation = translation.value))
                .observeOn(AndroidSchedulers.mainThread())

    fun addWords(words: List<Word>) =
            wordRepository.addWords(words)
                    .observeOn(AndroidSchedulers.mainThread())

    fun addWordsFromFile(uri: Uri) =
            readWordsFromFile(uri)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable { wordRepository.addWords(it) }
                .observeOn(AndroidSchedulers.mainThread())

    private fun readWordsFromFile(uri: Uri) = Single.create<ArrayList<Word>> { emitter ->
        val wordBunch: ArrayList<Word> = arrayListOf()

        try {
            val reader = CSVReaderBuilder(InputStreamReader(FileInputStream(File(URI(uri.toString()))), "UTF-8"))
                    .withCSVParser(CSVParserBuilder()
                            .withSeparator(';')
                            .build()
                    )
                    .build()
            var nextLine: Array<String?>?
            while (reader.readNext().also { nextLine = it } != null) { // nextLine[] is an array of values from the line
                nextLine?.let { wordBunch.add(Word(word = it[0], translation = it[1])) }
            }
            emitter.onSuccess(wordBunch.apply { reverse() })
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

}

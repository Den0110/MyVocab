package com.myvocab.myvocab.ui.add_new_word

import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.TranslationRepository
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Resource
import com.opencsv.CSVParserBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import com.opencsv.CSVReaderBuilder
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddNewWordViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val translationRepository: TranslationRepository
) : ViewModel() {

    val newWord: MutableLiveData<String> = MutableLiveData()
    val transcription: MutableLiveData<String> = MutableLiveData()
    val translation: MutableLiveData<String> = MutableLiveData()

    val meanings: MutableLiveData<String> = MutableLiveData()
    val synonyms: MutableLiveData<String> = MutableLiveData()
    val examples: MutableLiveData<MutableList<Word.Example>> = MutableLiveData()

    val suggestedWord: MediatorLiveData<Resource<Word>> = MediatorLiveData()

    var initialWord: Word? = null

    val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable.add(Observable.create<String> { emitter ->
            suggestedWord.addSource(newWord) {
                emitter.onNext(it)
            }
        }
        .debounce(700, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext { suggestedWord.value = Resource.loading() }
        .switchMapSingle {
            translationRepository
                    .translateInDictionary(TranslatableText(it, "en-ru"))
                    .onErrorReturnItem(Word.EMPTY)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            if(!it.isEmpty()) {
                suggestedWord.value = Resource.success(it)
            } else {
                suggestedWord.value = Resource.error(Exception("Word doesn't have a translation"))
            }
        })
    }

    fun initWith(word: Word?){
        word?.let {
            initialWord = it
            newWord.value = it.word
            transcription.value = it.transcription
            translation.value = it.translation
            meanings.value = it.meanings.joinToString(", ")
            synonyms.value = it.synonyms.joinToString(", ")
            examples.value = it.examples.toMutableList()
        }
    }

    fun fillFieldsWithSuggestedWord(){
        suggestedWord.value?.data?.let{
            transcription.value = it.transcription
            translation.value = it.translation
            meanings.value = it.meanings.joinToString(", ")
            synonyms.value = it.synonyms.joinToString(", ")
            examples.value = it.examples.toMutableList()
        }
    }

    fun commitWord(): Completable {

        val word = Word(
                word = newWord.value ?: "",
                translation = translation.value ?: "",
                transcription = transcription.value ?: "",
                meanings = (meanings.value ?: "").split(",").map { it.trim() },
                synonyms = (synonyms.value ?: "").split(",").map { it.trim() },
                examples = examples.value?.mapNotNull {
                    if (it.text.isEmpty() || it.translation.isEmpty()) null else it
                } ?: listOf()
        )

        initialWord?.let{
            return wordRepository.updateWord(it.copy(
                    word = word.word,
                    translation = word.translation,
                    transcription = word.transcription,
                    meanings = word.meanings,
                    synonyms = word.synonyms,
                    examples = word.examples
            )).observeOn(AndroidSchedulers.mainThread())
        }

        return wordRepository.addMyWord(word).observeOn(AndroidSchedulers.mainThread())
    }

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
                nextLine?.let { wordBunch.add(Word(word = it[0] ?: "", translation = it[1] ?: "")) }
            }
            emitter.onSuccess(wordBunch.apply { reverse() })
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

}

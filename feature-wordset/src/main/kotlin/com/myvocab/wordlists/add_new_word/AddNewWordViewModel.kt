package com.myvocab.wordlists.add_new_word

import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.domain.common.Resource
import com.myvocab.domain.entities.TranslatableText
import com.myvocab.domain.entities.Word
import com.myvocab.domain.repositories.TranslationRepository
import com.myvocab.domain.repositories.WordRepository
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URI
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

    init {
        viewModelScope.launch {
            callbackFlow<String> {
                suggestedWord.addSource(newWord) {
                    trySend(it)
                }
                awaitClose { }
            }
                .debounce(700)
                .distinctUntilChanged()
                .onEach { suggestedWord.value = Resource.Loading() }
                .collectLatest {
                    try {
                        val word = translationRepository.translateInDictionary(TranslatableText(it, "en-ru"))
                        suggestedWord.value = Resource.Success(word)
                    } catch (e: Exception) {
                        suggestedWord.value = Resource.Error(e)
                    }
                }
        }
    }

    fun initWith(word: Word?) {
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

    fun fillFieldsWithSuggestedWord() {
        suggestedWord.value?.data?.let {
            transcription.value = it.transcription
            translation.value = it.translation
            meanings.value = it.meanings.joinToString(", ")
            synonyms.value = it.synonyms.joinToString(", ")
            examples.value = it.examples.toMutableList()
        }
    }

    suspend fun commitWord() {
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

        initialWord?.let {
            return wordRepository.updateWord(
                it.copy(
                    word = word.word,
                    translation = word.translation,
                    transcription = word.transcription,
                    meanings = word.meanings,
                    synonyms = word.synonyms,
                    examples = word.examples
                )
            )
        }

        return wordRepository.addMyWord(word)
    }

    suspend fun addWordsFromFile(uri: Uri) =
        wordRepository.addWords(readWordsFromFile(uri))

    private suspend fun readWordsFromFile(uri: Uri): List<Word> = withContext(Dispatchers.IO) {
        val wordBunch: ArrayList<Word> = arrayListOf()

        val reader = CSVReaderBuilder(InputStreamReader(FileInputStream(File(URI(uri.toString()))), "UTF-8"))
            .withCSVParser(
                CSVParserBuilder()
                    .withSeparator(';')
                    .build()
            )
            .build()
        var nextLine: Array<String?>?
        while (reader.readNext().also { nextLine = it } != null) { // nextLine[] is an array of values from the line
            nextLine?.let { wordBunch.add(Word(word = it[0] ?: "", translation = it[1] ?: "")) }
        }
        wordBunch.apply { reverse() }
    }

}

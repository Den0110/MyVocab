package com.myvocab.myvocab.ui.fast_translation

import androidx.lifecycle.*
import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.TranslatedData
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.data.source.remote.translation.TranslationApi
import io.reactivex.Flowable
import javax.inject.Inject

class FastTranslationWidgetViewModel
@Inject
constructor(
        private val translationApi: TranslationApi,
        private val wordsRepository: WordRepository
) : ViewModel() {

    companion object {
        const val TAG = "FastTranslationService"
    }

    fun translate(translatable: TranslatableText): Flowable<TranslatedData> {
        return translationApi.translate(BuildConfig.GOOGLE_API_KEY, translatable.text, translatable.lang)
                .doOnNext { it.translatable = translatable }
    }

    fun addToDictionary(translatedData: TranslatedData) {
        wordsRepository
                .addWord(
                    Word(
                        word = translatedData.translatable?.text,
                        translation = translatedData.data?.translations?.get(0)?.translatedText
                    )
                )
                .subscribe()
    }

}
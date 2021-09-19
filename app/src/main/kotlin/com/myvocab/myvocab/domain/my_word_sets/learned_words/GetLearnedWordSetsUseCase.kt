package com.myvocab.myvocab.domain.my_word_sets.learned_words

import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.my_word_sets.GetMyWordSetsUseCase
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
import com.myvocab.myvocab.util.RepositoryData
import io.reactivex.Single
import javax.inject.Inject

class GetLearnedWordSetsUseCase
@Inject
constructor(
        private val wordRepository: WordRepository,
        getWordSetUseCase: GetWordSetUseCase
) : GetMyWordSetsUseCase(getWordSetUseCase) {

    override fun fetchWordSets(): Single<List<RepositoryData<WordSet>>> =
            wordRepository.getLearnedWordSets()

}
package com.myvocab.domain.usecases.learning

import com.myvocab.domain.entities.Word

data class LearningWordUseCaseResult(
    val word: Word,
    val wordSetTitle: String
)
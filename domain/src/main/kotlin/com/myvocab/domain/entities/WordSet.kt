package com.myvocab.domain.entities

import java.io.Serializable

data class WordSet(
        val globalId: String = "",
        val localId: Int? = null,
        val title: String = "",
        val words: List<Word> = emptyList()
) : Serializable
package com.myvocab.myvocab.data.model

data class NetworkDictionaryModel(
        var def: List<Entry>? = null
) {

    data class Entry(
            val text: String,
            val ts: String?,
            val tr: List<Translation>
    )

    data class Translation(
            val text: String,
            val syn: List<Text>?,
            val mean: List<Text>?,
            val ex: List<Example>?
    )

    data class Example(
            val text: String,
            val tr: List<Text>
    )

    data class Text(
            val text: String
    )
}
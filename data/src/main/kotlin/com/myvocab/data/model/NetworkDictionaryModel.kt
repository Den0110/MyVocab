package com.myvocab.data.model

import com.squareup.moshi.Json

data class NetworkDictionaryModel(
    @Json(name = "def") var def: List<Entry>? = null
) {

    data class Entry(
        @Json(name = "text") val text: String,
        @Json(name = "ts") val ts: String?,
        @Json(name = "tr") val tr: List<Translation>
    )

    data class Translation(
        @Json(name = "text") val text: String,
        @Json(name = "syn") val syn: List<Text>?,
        @Json(name = "mean") val mean: List<Text>?,
        @Json(name = "ex") val ex: List<Example>?
    )

    data class Example(
        @Json(name = "text") val text: String,
        @Json(name = "tr") val tr: List<Text>
    )

    data class Text(
        @Json(name = "text") val text: String
    )
}
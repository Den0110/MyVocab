package com.myvocab.data.source.remote.translation

import com.myvocab.data.model.NetworkDictionaryModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DictionaryApi {

    @FormUrlEncoded
    @POST("/api/v1/dicservice.json/lookup")
    suspend fun translate(@Field("key") key: String?,
                  @Field("text") source_text: String?,
                  @Field("lang") target_lang: String?): NetworkDictionaryModel

}
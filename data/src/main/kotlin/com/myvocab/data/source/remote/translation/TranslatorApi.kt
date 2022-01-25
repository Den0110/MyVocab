package com.myvocab.data.source.remote.translation

import com.myvocab.data.model.NetworkTranslatorModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TranslatorApi {

    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    suspend fun translate(@Field("key") key: String?,
                  @Field("text") source_text: String?,
                  @Field("lang") target_lang: String?): NetworkTranslatorModel

}

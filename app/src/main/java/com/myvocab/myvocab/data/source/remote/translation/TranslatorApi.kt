package com.myvocab.myvocab.data.source.remote.translation

import com.myvocab.myvocab.data.model.NetworkTranslatorModel
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TranslatorApi {

    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    fun translate(@Field("key") key: String?,
                  @Field("text") source_text: String?,
                  @Field("lang") target_lang: String?): Single<NetworkTranslatorModel>

}

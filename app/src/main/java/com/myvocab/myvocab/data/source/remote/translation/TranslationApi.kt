package com.myvocab.myvocab.data.source.remote.translation

import com.myvocab.myvocab.data.model.TranslatedData

import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TranslationApi {

    @FormUrlEncoded
    @POST("/language/translate/v2")
    fun translate(@Field("key") key: String?,
                  @Field("q") source_text: String?,
                  @Field("target") target_lang: String?): Flowable<TranslatedData>

}

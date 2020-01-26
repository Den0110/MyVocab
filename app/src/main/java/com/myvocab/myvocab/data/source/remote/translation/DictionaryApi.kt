package com.myvocab.myvocab.data.source.remote.translation

import com.myvocab.myvocab.data.model.DictionaryModel
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DictionaryApi {

    @FormUrlEncoded
    @POST("/api/v1/dicservice.json/lookup")
    fun translate(@Field("key") key: String?,
                  @Field("text") source_text: String?,
                  @Field("lang") target_lang: String?): Single<DictionaryModel>

}
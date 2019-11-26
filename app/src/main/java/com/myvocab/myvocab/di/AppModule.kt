package com.myvocab.myvocab.di

import android.app.Application
import androidx.room.Room
import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.data.source.local.Database
import com.myvocab.myvocab.data.source.local.WordsDao
import com.myvocab.myvocab.util.Constants

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(BuildConfig.GOOGLE_API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideWordsDatabase(app: Application): Database =
            Room.databaseBuilder(app, Database::class.java, "words_db").build()

    @Provides
    @Singleton
    fun provideWordsDao(database: Database): WordsDao = database.wordsDao()

    @Provides
    @Singleton
    fun provideWordRepository(wordsDao: WordsDao) : WordRepository = WordRepository(wordsDao)

}

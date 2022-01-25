package com.myvocab.data.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myvocab.core.BuildConfig
import com.myvocab.data.model.WordSetDbModel
import com.myvocab.data.source.TranslationRepositoryImpl
import com.myvocab.data.source.WordRepositoryImpl
import com.myvocab.data.source.WordSetRepositoryImpl
import com.myvocab.data.source.local.*
import com.myvocab.data.source.remote.translation.DictionaryApi
import com.myvocab.data.source.remote.translation.TranslatorApi
import com.myvocab.data.source.remote.wordset.WordSetRemoteDataSource
import com.myvocab.domain.repositories.TranslationRepository
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.domain.repositories.WordSetRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    @Named("translator")
    fun provideTranslatorRetrofitInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl(BuildConfig.YANDEX_TRANSLATE_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("dictionary")
    fun provideDictionaryRetrofitInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl(BuildConfig.YANDEX_DICTIONARY_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private lateinit var wordsDb: Database

    @Provides
    @Singleton
    fun provideWordsDatabase(app: Application): Database {
        wordsDb = Room.databaseBuilder(app, Database::class.java, "words_db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Executors.newSingleThreadScheduledExecutor().execute {
                        Timber.d("Database created")
                        GlobalScope.launch {
                            wordsDb
                                .wordSetsDao()
                                .addWordSet(WordSetDbModel(globalId = WordSetDbModel.MY_WORDS, title = "My words"))
                        }
                    }
                }
            })
            .addMigrations(Database.MIGRATION_3_4, Database.MIGRATION_4_5)
            .build()
        return wordsDb
    }

    @Provides
    @Singleton
    fun provideWordsDao(database: Database): WordDao = database.wordsDao()

    @Provides
    @Singleton
    fun provideWordSetsDao(database: Database): WordSetDao = database.wordSetsDao()

    @Provides
    @Singleton
    fun provideWordRepository(
        wordLocalDataSource: WordLocalDataSource
    ): WordRepository = WordRepositoryImpl(wordLocalDataSource)

    @Provides
    @Singleton
    fun provideWordSetRepository(
        wordSetLocalDataSource: WordSetLocalDataSource,
        wordSetRemoteDataSource: WordSetRemoteDataSource,
    ): WordSetRepository = WordSetRepositoryImpl(wordSetLocalDataSource, wordSetRemoteDataSource)

    @Provides
    fun provideTranslateApi(
        @Named("translator") retrofit: Retrofit
    ): TranslatorApi = retrofit.create(TranslatorApi::class.java)

    @Provides
    fun provideDictionaryApi(
        @Named("dictionary") retrofit: Retrofit
    ): DictionaryApi = retrofit.create(DictionaryApi::class.java)

    @Provides
    @Singleton
    fun provideTranslationRepository(
        translatorApi: TranslatorApi,
        dictionaryApi: DictionaryApi
    ): TranslationRepository = TranslationRepositoryImpl(translatorApi, dictionaryApi)

}
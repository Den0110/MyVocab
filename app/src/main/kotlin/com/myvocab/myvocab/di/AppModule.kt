package com.myvocab.myvocab.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.common.FastTranslationServiceManager
import com.myvocab.myvocab.common.ReminderScheduler
import com.myvocab.myvocab.data.model.WordSetDbModel
import com.myvocab.myvocab.data.source.TranslationRepository
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.data.source.local.Database
import com.myvocab.myvocab.data.source.local.WordDao
import com.myvocab.myvocab.data.source.local.WordSetDao
import com.myvocab.myvocab.data.source.remote.translation.DictionaryApi
import com.myvocab.myvocab.data.source.remote.translation.TranslatorApi
import com.myvocab.myvocab.system.ResourceManager
import com.myvocab.myvocab.util.PreferencesManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
                            wordsDb.wordSetsDao().addWordSet(WordSetDbModel(globalId = WordSetDbModel.MY_WORDS, title = "My words")).subscribe()
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
    fun provideWordRepository(wordDao: WordDao, wordSetDao: WordSetDao) = WordRepository(wordDao, wordSetDao)

    @Provides
    fun provideTranslateApi(@Named("translator") retrofit: Retrofit): TranslatorApi = retrofit.create(TranslatorApi::class.java)

    @Provides
    fun provideDictionaryApi(@Named("dictionary") retrofit: Retrofit): DictionaryApi = retrofit.create(DictionaryApi::class.java)

    @Provides
    @Singleton
    fun provideTranslationRepository(translatorApi: TranslatorApi, dictionaryApi: DictionaryApi) =
            TranslationRepository(translatorApi, dictionaryApi)

    @Provides
    @Singleton
    fun providePreferencesManager(app: Application) = PreferencesManager(app)

    @Provides
    @Singleton
    fun provideReminderScheduler(app: Application, prefManager: PreferencesManager) = ReminderScheduler(app, prefManager)

    @Provides
    @Singleton
    fun provideFastTranslationServiceManager(app: Application, prefManager: PreferencesManager) = FastTranslationServiceManager(app, prefManager)

    @Provides
    @Singleton
    fun provideResourceManager(app: Application) = ResourceManager(app)

}

package com.skyyaros.skillcinema

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.skyyaros.skillcinema.data.AppDatabase
import com.skyyaros.skillcinema.data.DatabaseRepository
import com.skyyaros.skillcinema.data.KinopoiskApi
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.StoreRepository
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DaggerModule::class],
)
interface DaggerComponent {
    fun getStoreRepository(): StoreRepository
    fun getKinopoiskRepository(): KinopoiskRepository
    fun getDatabaseRepository(): DatabaseRepository
}

@Module
class DaggerModule(private val dataStore: DataStore<Preferences>, private val context: Context) {
    @Provides
    @Singleton
    fun storeRepository(): StoreRepository = StoreRepository(dataStore)

    @Provides
    @Singleton
    fun kinopoiskApi(): KinopoiskApi = KinopoiskApi.provide()

    @Provides
    @Singleton
    fun kinopoiskRepository(kinopoiskApi: KinopoiskApi): KinopoiskRepository = KinopoiskRepository(kinopoiskApi)

    @Provides
    @Singleton
    fun appDatabase(): AppDatabase = AppDatabase.provide(context)

    @Provides
    @Singleton
    fun databaseRepository(appDatabase: AppDatabase): DatabaseRepository = DatabaseRepository(appDatabase)
}
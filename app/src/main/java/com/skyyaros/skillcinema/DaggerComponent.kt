package com.skyyaros.skillcinema

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.skyyaros.skillcinema.data.AppDatabase
import com.skyyaros.skillcinema.data.DatabaseRepository
import com.skyyaros.skillcinema.data.KinopoiskApi
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.data.StoreRepositoryDefault
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DaggerModule::class],
)
interface DaggerComponent {
    fun getStoreRepository(): StoreRepositoryDefault
    fun getKinopoiskRepository(): KinopoiskRepositoryDefault
    fun getDatabaseRepository(): DatabaseRepository
}

@Module
class DaggerModule(private val dataStore: DataStore<Preferences>, private val context: Context) {
    @Provides
    @Singleton
    fun storeRepository(): StoreRepositoryDefault = StoreRepositoryDefault(dataStore)

    @Provides
    @Singleton
    fun kinopoiskApi(): KinopoiskApi = KinopoiskApi.provide()

    @Provides
    @Singleton
    fun kinopoiskRepository(kinopoiskApi: KinopoiskApi): KinopoiskRepositoryDefault = KinopoiskRepositoryDefault(kinopoiskApi)

    @Provides
    @Singleton
    fun appDatabase(): AppDatabase = AppDatabase.provide(context)

    @Provides
    @Singleton
    fun databaseRepository(appDatabase: AppDatabase): DatabaseRepository = DatabaseRepository(appDatabase)
}
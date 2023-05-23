package com.skyyaros.skillcinema

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
}

@Module
class DaggerModule(private val dataStore: DataStore<Preferences>) {
    @Provides
    @Singleton
    fun storeRepository(): StoreRepository = StoreRepository(dataStore)

    @Provides
    @Singleton
    fun kinopoiskApi(): KinopoiskApi = KinopoiskApi.provide()

    @Provides
    @Singleton
    fun kinopoiskRepository(kinopoiskApi: KinopoiskApi): KinopoiskRepository = KinopoiskRepository(kinopoiskApi)
}
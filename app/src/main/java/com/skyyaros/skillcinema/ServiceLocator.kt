package com.skyyaros.skillcinema

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.StoreRepository

object ServiceLocator {
    @VisibleForTesting
    var fakeKinopoiskRepository: KinopoiskRepository? = null
    @VisibleForTesting
    var fakeStoreRepository: StoreRepository? = null

    fun provideKinopoiskRepository(): KinopoiskRepository {
        synchronized(this) {
            return fakeKinopoiskRepository ?: App.component.getKinopoiskRepository()
        }
    }

    fun provideStoreRepository(): StoreRepository {
        synchronized(this) {
            return fakeStoreRepository ?: App.component.getStoreRepository()
        }
    }
}
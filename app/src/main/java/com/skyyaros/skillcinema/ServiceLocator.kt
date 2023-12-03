package com.skyyaros.skillcinema

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.StoreRepository
import com.skyyaros.skillcinema.ui.ActivityCallbacks

object ServiceLocator {
    @VisibleForTesting
    var fakeKinopoiskRepository: KinopoiskRepository? = null
    @VisibleForTesting
    var fakeStoreRepository: StoreRepository? = null
    @VisibleForTesting
    var fakeActivityCallbacks: ActivityCallbacks? = null

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

    fun provideActivityCallbacks(): ActivityCallbacks? {
        synchronized(this) {
            return fakeActivityCallbacks
        }
    }
}
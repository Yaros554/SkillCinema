package com.skyyaros.skillcinema

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore

private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class App: Application() {
    companion object {
        lateinit var component: DaggerComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerDaggerComponent.builder()
            .daggerModule(DaggerModule(dataStore, this))
            .build()
    }
}
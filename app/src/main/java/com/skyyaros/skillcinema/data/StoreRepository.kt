package com.skyyaros.skillcinema.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

class StoreRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val IS_FIRST_START = booleanPreferencesKey("is_first_start")
    }

    suspend fun getStartStatus(): Boolean {
        val preferences = dataStore.data.first().toPreferences()
        val showCompleted = preferences[PreferencesKeys.IS_FIRST_START] ?: true
        return showCompleted
    }

    suspend fun setStartStatus(status: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_START] = status
        }
    }
}
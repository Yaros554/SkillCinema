package com.skyyaros.skillcinema.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StoreRepositoryDefault(private val dataStore: DataStore<Preferences>) : StoreRepository {
    private object PreferencesKeys {
        val IS_FIRST_START = booleanPreferencesKey("is_first_start")
        val SHOW_DIALOG_PHOTO = intPreferencesKey("show_dialog_photo")
        val SHOW_DIALOG_VIDEO = intPreferencesKey("show_dialog_video")
    }

    override suspend fun getStartStatus(): Boolean {
        val preferences = dataStore.data.first().toPreferences()
        val showCompleted = preferences[PreferencesKeys.IS_FIRST_START] ?: true
        return showCompleted
    }

    override suspend fun setStartStatus(status: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_START] = status
        }
    }

    override fun getDialogStatusFlow(mode: Int): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[if (mode == 1) PreferencesKeys.SHOW_DIALOG_PHOTO else PreferencesKeys.SHOW_DIALOG_VIDEO] ?: 0
        }
    }

    override suspend fun setDialogStatus(mode: Int, status: Int) {
        dataStore.edit { preferences ->
            preferences[if (mode == 1) PreferencesKeys.SHOW_DIALOG_PHOTO else PreferencesKeys.SHOW_DIALOG_VIDEO] = status
        }
    }
}
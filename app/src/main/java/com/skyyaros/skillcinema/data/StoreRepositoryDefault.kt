package com.skyyaros.skillcinema.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.skyyaros.skillcinema.entity.AppSettings
import com.skyyaros.skillcinema.entity.AppTheme
import com.skyyaros.skillcinema.entity.VideoSource
import com.skyyaros.skillcinema.ui.FullscreenDialogInfoMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StoreRepositoryDefault(private val dataStore: DataStore<Preferences>) : StoreRepository {
    private object PreferencesKeys {
        val IS_FIRST_START = booleanPreferencesKey("is_first_start")
        val SHOW_DIALOG_PHOTO = booleanPreferencesKey("show_dialog_photo")
        val SHOW_DIALOG_VIDEO = booleanPreferencesKey("show_dialog_video")
        val VIDEO_SOURCE = stringPreferencesKey("video_source")
        val CURRENT_THEME = stringPreferencesKey("current_theme")
        val ANIM_ACTIVE = booleanPreferencesKey("anim_active")
    }

    override fun getStartStatus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.IS_FIRST_START] ?: true
        }
    }

    override suspend fun setStartStatus(status: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_START] = status
        }
    }

    override fun getDialogStatusFlow(mode: FullscreenDialogInfoMode): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[
                if (mode == FullscreenDialogInfoMode.PHOTO)
                    PreferencesKeys.SHOW_DIALOG_PHOTO
                else
                    PreferencesKeys.SHOW_DIALOG_VIDEO
            ] ?: true
        }
    }

    override suspend fun setDialogStatus(mode: FullscreenDialogInfoMode, isShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[
                if (mode == FullscreenDialogInfoMode.PHOTO)
                    PreferencesKeys.SHOW_DIALOG_PHOTO
                else
                    PreferencesKeys.SHOW_DIALOG_VIDEO
            ] = isShow
        }
    }

    override fun getAppSettingsFlow(): Flow<AppSettings> {
        return dataStore.data.map { preferences ->
            val curSource = preferences[PreferencesKeys.VIDEO_SOURCE] ?: VideoSource.ANY.name
            val curTheme = preferences[PreferencesKeys.CURRENT_THEME] ?: AppTheme.AUTO.name
            val animActive = preferences[PreferencesKeys.ANIM_ACTIVE] ?: true
            AppSettings(VideoSource.valueOf(curSource), AppTheme.valueOf(curTheme), animActive)
        }
    }

    override suspend fun setAppSettings(appSettings: AppSettings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIDEO_SOURCE] = appSettings.videoSource.name
            preferences[PreferencesKeys.CURRENT_THEME] = appSettings.theme.name
            preferences[PreferencesKeys.ANIM_ACTIVE] = appSettings.animActive
        }
    }
}
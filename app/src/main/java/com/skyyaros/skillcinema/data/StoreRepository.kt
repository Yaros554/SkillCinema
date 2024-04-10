package com.skyyaros.skillcinema.data

import com.skyyaros.skillcinema.entity.AppSettings
import com.skyyaros.skillcinema.ui.FullscreenDialogInfoMode
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun getStartStatus(): Flow<Boolean>

    suspend fun setStartStatus(status: Boolean)
    fun getDialogStatusFlow(mode: FullscreenDialogInfoMode): Flow<Boolean>

    suspend fun setDialogStatus(mode: FullscreenDialogInfoMode, isShow: Boolean)

    fun getAppSettingsFlow(): Flow<AppSettings>

    suspend fun setAppSettings(appSettings: AppSettings)
}
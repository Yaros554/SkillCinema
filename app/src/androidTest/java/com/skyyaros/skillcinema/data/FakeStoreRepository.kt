package com.skyyaros.skillcinema.data

import com.skyyaros.skillcinema.entity.AppSettings
import com.skyyaros.skillcinema.ui.FullscreenDialogInfoMode
import kotlinx.coroutines.flow.Flow

class FakeStoreRepository: StoreRepository {
    private var isFirst = false
    override fun getStartStatus(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun setStartStatus(status: Boolean) {
        isFirst = status
    }

    override fun getDialogStatusFlow(mode: FullscreenDialogInfoMode): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun setDialogStatus(mode: FullscreenDialogInfoMode, isShow: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getAppSettingsFlow(): Flow<AppSettings> {
        TODO("Not yet implemented")
    }

    override suspend fun setAppSettings(appSettings: AppSettings) {
        TODO("Not yet implemented")
    }
}
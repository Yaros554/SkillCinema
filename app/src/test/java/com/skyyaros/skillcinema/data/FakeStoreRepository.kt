package com.skyyaros.skillcinema.data

import kotlinx.coroutines.flow.Flow

class FakeStoreRepository: StoreRepository {
    private var isFirst = false
    override suspend fun getStartStatus(): Boolean {
        return isFirst
    }

    override suspend fun setStartStatus(status: Boolean) {
        isFirst = status
    }

    override fun getDialogStatusFlow(mode: Int): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun setDialogStatus(mode: Int, status: Int) {
        TODO("Not yet implemented")
    }
}
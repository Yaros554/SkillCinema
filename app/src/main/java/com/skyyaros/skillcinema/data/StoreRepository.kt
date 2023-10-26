package com.skyyaros.skillcinema.data

import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    suspend fun getStartStatus(): Boolean

    suspend fun setStartStatus(status: Boolean)
    fun getDialogStatusFlow(mode: Int): Flow<Int>

    suspend fun setDialogStatus(mode: Int, status: Int)
}
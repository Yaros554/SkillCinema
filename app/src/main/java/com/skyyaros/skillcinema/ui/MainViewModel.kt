package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.entity.DopInfoForFilm
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MainViewModel(private val kinopoiskRepository: KinopoiskRepository): ViewModel() {
    private val dopInfo = mutableMapOf<Long, DopInfoForFilm>()
    private val mutex = Mutex()
    var isInit = false

    suspend fun getDopInfoForFilm(id: Long): DopInfoForFilm? {
        return if (dopInfo.containsKey(id))
            dopInfo[id]
        else {
            val res = kinopoiskRepository.getDopInfoForFilm(id)
            if (res != null) {
                mutex.withLock { dopInfo += Pair(id, res) }
                dopInfo[id]
            } else {
                null
            }
        }
    }
}
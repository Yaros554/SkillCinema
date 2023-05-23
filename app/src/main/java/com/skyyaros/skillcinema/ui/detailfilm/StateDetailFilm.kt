package com.skyyaros.skillcinema.ui.detailfilm

import com.skyyaros.skillcinema.entity.FullDetailFilm

sealed class StateDetailFilm {
    object Loading: StateDetailFilm()
    data class Error(val message: String): StateDetailFilm()
    data class Success(val data: FullDetailFilm): StateDetailFilm()
}
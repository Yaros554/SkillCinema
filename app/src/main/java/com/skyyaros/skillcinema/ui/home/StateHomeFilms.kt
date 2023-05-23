package com.skyyaros.skillcinema.ui.home

import com.skyyaros.skillcinema.entity.FilmsPreviewWithData

sealed class StateHomeFilms{
    object Loading: StateHomeFilms()
    data class Error(val message: String): StateHomeFilms()
    data class Success(val data: FilmsPreviewWithData): StateHomeFilms()
}

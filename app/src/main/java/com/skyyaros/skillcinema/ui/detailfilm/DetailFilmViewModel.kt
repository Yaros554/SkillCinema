package com.skyyaros.skillcinema.ui.detailfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.ui.home.StateHomeFilms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailFilmViewModel(private val kinopoiskRepository: KinopoiskRepository, private val id: Long): ViewModel() {
    private val _detailFilmFlow = MutableStateFlow<StateDetailFilm>(StateDetailFilm.Loading)
    val detailFilmFlow = _detailFilmFlow.asStateFlow()
    var isCollapsing = true
    var animationActive = false

    init {
        viewModelScope.launch {
            val result = kinopoiskRepository.getFullDetailFilm(id)
            if (result != null) {
                _detailFilmFlow.emit(StateDetailFilm.Success(result))
            } else {
                _detailFilmFlow.emit(StateDetailFilm.Error("Ошибка загрузки!"))
            }
        }
    }

    fun reloadFilm() {
        viewModelScope.launch {
            _detailFilmFlow.emit(StateDetailFilm.Loading)
            val result = kinopoiskRepository.getFullDetailFilm(id)
            if (result != null) {
                _detailFilmFlow.emit(StateDetailFilm.Success(result))
            } else {
                _detailFilmFlow.emit(StateDetailFilm.Error("Ошибка загрузки!"))
            }
        }
    }
}
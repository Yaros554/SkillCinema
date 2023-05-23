package com.skyyaros.skillcinema.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val storeRepository: StoreRepository, private val kinopoiskRepository: KinopoiskRepository): ViewModel() {
    private val _statusStartFlow = MutableStateFlow(false)
    val statusStartFlow = _statusStartFlow.asStateFlow()
    private val _filmsFlow = MutableStateFlow<StateHomeFilms>(StateHomeFilms.Loading)
    val filmsFlow = _filmsFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val status = storeRepository.getStartStatus()
            _statusStartFlow.emit(status)
        }
        viewModelScope.launch {
            val films = kinopoiskRepository.getFilmsForHome()
            if (films != null) {
                _filmsFlow.emit(StateHomeFilms.Success(films))
            } else {
                _filmsFlow.emit(StateHomeFilms.Error("Ошибка загрузки!"))
            }
        }
    }

    fun setStartStatus(status: Boolean) {
        viewModelScope.launch {
            storeRepository.setStartStatus(status)
        }
    }

    fun updateFilms() {
        viewModelScope.launch {
            _filmsFlow.emit(StateHomeFilms.Loading)
            val films = kinopoiskRepository.getFilmsForHome()
            if (films != null) {
                _filmsFlow.emit(StateHomeFilms.Success(films))
            } else {
                _filmsFlow.emit(StateHomeFilms.Error("Ошибка загрузки!"))
            }
        }
    }
}
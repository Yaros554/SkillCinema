package com.skyyaros.skillcinema.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.data.StoreRepository
import com.skyyaros.skillcinema.data.StoreRepositoryDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val storeRepositoryDefault: StoreRepository, private val kinopoiskRepositoryDefault: KinopoiskRepository): ViewModel() {
    private val _statusStartFlow = MutableStateFlow(false)
    val statusStartFlow = _statusStartFlow.asStateFlow()
    private val _filmsFlow = MutableStateFlow<StateHomeFilms>(StateHomeFilms.Loading)
    val filmsFlow = _filmsFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val status = storeRepositoryDefault.getStartStatus()
            _statusStartFlow.emit(status)
        }
        viewModelScope.launch {
            val films = kinopoiskRepositoryDefault.getFilmsForHome()
            if (films != null) {
                _filmsFlow.emit(StateHomeFilms.Success(films))
            } else {
                _filmsFlow.emit(StateHomeFilms.Error("Ошибка загрузки!"))
            }
        }
    }

    fun setStartStatus(status: Boolean) {
        viewModelScope.launch {
            storeRepositoryDefault.setStartStatus(status)
        }
    }

    fun updateFilms() {
        _filmsFlow.value = StateHomeFilms.Loading
        viewModelScope.launch {
            val films = kinopoiskRepositoryDefault.getFilmsForHome()
            if (films != null) {
                _filmsFlow.emit(StateHomeFilms.Success(films))
            } else {
                _filmsFlow.emit(StateHomeFilms.Error("Ошибка загрузки!"))
            }
        }
    }
}
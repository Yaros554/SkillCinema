package com.skyyaros.skillcinema.ui.detailactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.ui.detailfilm.StateDetailFilm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActorDetailViewModel(private val kinopoiskRepository: KinopoiskRepository, private val id: Long): ViewModel() {
    private val _detailActorFlow = MutableStateFlow<StateDetailActor>(StateDetailActor.Loading)
    val detailActorFlow = _detailActorFlow.asStateFlow()
    var isCollapsing = true
    var animationActive = false

    init {
        viewModelScope.launch {
            val result = kinopoiskRepository.getActorDetail(id)
            if (result != null) {
                _detailActorFlow.emit(StateDetailActor.Success(result))
            } else {
                _detailActorFlow.emit(StateDetailActor.Error("Ошибка загрузки!"))
            }
        }
    }

    fun reloadActor() {
        viewModelScope.launch {
            _detailActorFlow.emit(StateDetailActor.Loading)
            val result = kinopoiskRepository.getActorDetail(id)
            if (result != null) {
                _detailActorFlow.emit(StateDetailActor.Success(result))
            } else {
                _detailActorFlow.emit(StateDetailActor.Error("Ошибка загрузки!"))
            }
        }
    }
}
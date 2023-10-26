package com.skyyaros.skillcinema.ui.detailactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.data.StoreRepositoryDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActorDetailViewModel(
    private val kinopoiskRepositoryDefault: KinopoiskRepositoryDefault,
    private val storeRepositoryDefault: StoreRepositoryDefault,
    private val id: Long
): ViewModel() {
    private val _detailActorFlow = MutableStateFlow<StateDetailActor>(StateDetailActor.Loading)
    val detailActorFlow = _detailActorFlow.asStateFlow()
    val statusPhotoDialogFlow = storeRepositoryDefault.getDialogStatusFlow(1).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )
    var isCollapsing = true
    var animationActive = false
    var name = ""
    var posterUrl = ""

    init {
        viewModelScope.launch {
            val result = kinopoiskRepositoryDefault.getActorDetail(id)
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
            val result = kinopoiskRepositoryDefault.getActorDetail(id)
            if (result != null) {
                _detailActorFlow.emit(StateDetailActor.Success(result))
            } else {
                _detailActorFlow.emit(StateDetailActor.Error("Ошибка загрузки!"))
            }
        }
    }

    fun setDialogStatus(status: Int) {
        viewModelScope.launch {
            storeRepositoryDefault.setDialogStatus(1, status)
        }
    }
}
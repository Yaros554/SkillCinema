package com.skyyaros.skillcinema.ui.detailfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.StoreRepository
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.entity.VideoItem
import com.skyyaros.skillcinema.ui.home.StateHomeFilms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailFilmViewModel(private val storeRepository: StoreRepository, private val kinopoiskRepository: KinopoiskRepository, private val id: Long): ViewModel() {
    private val _detailFilmFlow = MutableStateFlow<StateDetailFilm>(StateDetailFilm.Loading)
    val detailFilmFlow = _detailFilmFlow.asStateFlow()
    val statusPhotoDialogFlow = storeRepository.getDialogStatusFlow(1).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )
    val statusVideoDialogFlow = storeRepository.getDialogStatusFlow(2).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )
    var isCollapsing = true
    var animationActive = false
    var curVideoUrlSave: String? = null
    var listVideoItemsSave: List<VideoItem>? = null
    var curPhotoUrlSave: String? = null
    var listPhotoItemsSave: List<ImageItem>? = null

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

    fun setDialogStatus(mode: Int, status: Int) {
        viewModelScope.launch {
            storeRepository.setDialogStatus(mode, status)
        }
    }
}
package com.skyyaros.skillcinema.ui.detailfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.data.StoreRepositoryDefault
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.entity.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailFilmViewModel(private val storeRepositoryDefault: StoreRepositoryDefault, private val kinopoiskRepositoryDefault: KinopoiskRepositoryDefault, private val id: Long): ViewModel() {
    private val _detailFilmFlow = MutableStateFlow<StateDetailFilm>(StateDetailFilm.Loading)
    val detailFilmFlow = _detailFilmFlow.asStateFlow()
    val statusPhotoDialogFlow = storeRepositoryDefault.getDialogStatusFlow(1).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )
    val statusVideoDialogFlow = storeRepositoryDefault.getDialogStatusFlow(2).stateIn(
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
            val result = kinopoiskRepositoryDefault.getFullDetailFilm(id)
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
            val result = kinopoiskRepositoryDefault.getFullDetailFilm(id)
            if (result != null) {
                _detailFilmFlow.emit(StateDetailFilm.Success(result))
            } else {
                _detailFilmFlow.emit(StateDetailFilm.Error("Ошибка загрузки!"))
            }
        }
    }

    fun setDialogStatus(mode: Int, status: Int) {
        viewModelScope.launch {
            storeRepositoryDefault.setDialogStatus(mode, status)
        }
    }
}
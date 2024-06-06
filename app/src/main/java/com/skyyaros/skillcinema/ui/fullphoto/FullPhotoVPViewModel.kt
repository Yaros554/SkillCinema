package com.skyyaros.skillcinema.ui.fullphoto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.data.PhotoPagingSource
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlinx.coroutines.flow.Flow

class FullPhotoVPViewModel(
    type: String, id: Long, private val kinopoiskRepository: KinopoiskRepositoryDefault,
    activityCallbacks: ActivityCallbacks, private val stack: String
): ViewModel() {
    var fullScreen = true
    var isFirst = true
    var pagingPhotos: Flow<PagingData<ImageItem>>? = null
    var loadStates: CombinedLoadStates? = null
    init {
        if (id != -1L) {
            pagingPhotos = Pager(
                config = PagingConfig(pageSize = 18),
                pagingSourceFactory = {
                    val url = activityCallbacks.getUrlPosAnim(stack)
                    val page = kinopoiskRepository.getPageFromUrl(id, type, url)
                    PhotoPagingSource(kinopoiskRepository, id, type, page, "FullPhotoVPFragment", stack)
                }
            ).flow.cachedIn(viewModelScope)
        }
    }

    fun enablePreload() {
        kinopoiskRepository.denyEnablePreload(stack, null, true)
    }
}
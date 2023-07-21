package com.skyyaros.skillcinema.ui.fullphoto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.PhotoPagingSource
import com.skyyaros.skillcinema.entity.ImageItem
import kotlinx.coroutines.flow.Flow

class FullPhotoVPViewModel(preload: List<ImageItem>, type: String, id: Long, kinopoiskRepository: KinopoiskRepository, page_init: Int): ViewModel() {
    var fullScreen = true
    var isFirst = true
    var pagingPhotos: Flow<PagingData<ImageItem>>? = null
    init {
        if (id != -1L) {
            pagingPhotos = Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    PhotoPagingSource(
                        kinopoiskRepository,
                        id,
                        type,
                        preload,
                        page_init
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }
    }
}
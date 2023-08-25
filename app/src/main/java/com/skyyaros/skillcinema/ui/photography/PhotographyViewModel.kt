package com.skyyaros.skillcinema.ui.photography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.PhotoPagingSource
import com.skyyaros.skillcinema.data.StoreRepository
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.entity.ImageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotographyViewModel(
    listImageResponse: List<ImageResponse>, id: Long,
    kinopoiskRepository: KinopoiskRepository, private val storeRepository: StoreRepository
): ViewModel() {
    val pagedPhotos = mutableMapOf<String, Flow<PagingData<ImageItem>>>()
    val statusPhotoDialogFlow = storeRepository.getDialogStatusFlow(1).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )
    var title = ""
    var urls: List<ImageItem> = emptyList()
    var curUrl = ""
    init {
        listImageResponse.forEach {
            pagedPhotos += it.imageType!! to Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = { PhotoPagingSource(kinopoiskRepository, id, it.imageType!!, it.items) }
            ).flow.cachedIn(viewModelScope)
        }
    }

    fun setDialogStatus(status: Int) {
        viewModelScope.launch {
            storeRepository.setDialogStatus(1, status)
        }
    }
}
package com.skyyaros.skillcinema.ui.photography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.PhotoPagingSource
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.entity.ImageResponse
import kotlinx.coroutines.flow.Flow

class PhotographyViewModel(
    listImageResponse: List<ImageResponse>,
    id: Long, kinopoiskRepository: KinopoiskRepository
): ViewModel() {
    val pagedPhotos = mutableMapOf<String, Flow<PagingData<ImageItem>>>()
    init {
        listImageResponse.forEach {
            pagedPhotos += it.imageType!! to Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = { PhotoPagingSource(kinopoiskRepository, id, it.imageType!!, it.items) }
            ).flow.cachedIn(viewModelScope)
        }
    }
}
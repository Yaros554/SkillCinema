package com.skyyaros.skillcinema.ui.photography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.data.PhotoPagingSource
import com.skyyaros.skillcinema.data.StoreRepositoryDefault
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.entity.ImageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhotographyViewModel(
    listImageResponse: List<ImageResponse>, id: Long,
    kinopoiskRepositoryDefault: KinopoiskRepositoryDefault, private val storeRepositoryDefault: StoreRepositoryDefault
): ViewModel() {
    val pagedPhotos = mutableMapOf<String, Flow<PagingData<ImageItem>>>()
    val statusPhotoDialogFlow = storeRepositoryDefault.getDialogStatusFlow(1).stateIn(
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
                pagingSourceFactory = { PhotoPagingSource(kinopoiskRepositoryDefault, id, it.imageType!!, it.items) }
            ).flow.cachedIn(viewModelScope)
        }
    }

    fun setDialogStatus(status: Int) {
        viewModelScope.launch {
            storeRepositoryDefault.setDialogStatus(1, status)
        }
    }
}
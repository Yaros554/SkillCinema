package com.skyyaros.skillcinema.ui.photography

import android.widget.ImageView
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
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PhotographyViewModel(
    listImageTypes: List<String>,
    private val id: Long,
    private val kinopoiskRepository: KinopoiskRepositoryDefault,
    private val activityCallbacks: ActivityCallbacks,
    private val curStack: String
): ViewModel() {
    val pagedPhotos = mutableMapOf<String, Flow<PagingData<ImageItem>>>()
    val needUpdate = mutableMapOf<String, Boolean>()
    var title = ""
    var needPostpone = false
    var isFirst = true
    init {
        listImageTypes.forEach {
            kinopoiskRepository.addPermissionType(curStack, it)
            pagedPhotos += it to Pager(
                config = PagingConfig(pageSize = 18),
                pagingSourceFactory = { PhotoPagingSource(
                    kinopoiskRepository, id, it, 1, "PhotographyFragment", curStack
                ) }
            ).flow.cachedIn(viewModelScope)
            needUpdate += it to false
        }
    }

    fun updatePagingData(imageType: String) {
        pagedPhotos[imageType] = Pager(
            config = PagingConfig(pageSize = 18),
            pagingSourceFactory = {
                val page = kinopoiskRepository.getPageFromUrl(id, imageType, activityCallbacks.getUrlPosAnim(curStack))
                PhotoPagingSource(kinopoiskRepository, id, imageType, page, "PhotographyFragment", curStack)
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun enablePreload(imageType: String) {
        kinopoiskRepository.denyEnablePreload(curStack, imageType, true)
    }

    fun disablePreload(imageType: String) {
        kinopoiskRepository.denyEnablePreload(curStack, imageType, false)
        kinopoiskRepository.denyEnablePreload(curStack, null, false)
    }
}
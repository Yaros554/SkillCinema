package com.skyyaros.skillcinema.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyaros.skillcinema.entity.ImageItem
import kotlinx.coroutines.delay
import kotlin.math.min

class PhotoPagingSource(
    private val kinopoiskRepository: KinopoiskRepository,
    private val id: Long,
    private val type: String,
    private val listPhotoPreview: List<ImageItem>,
    private val page_init: Int = 1
): PagingSource<Int, ImageItem>() {
    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val page = params.key ?: page_init
        if (page <= (listPhotoPreview.size - 1) / 20 + 1) {
            if (params.key != null)
                delay(300)
            return LoadResult.Page(
                data = listPhotoPreview.subList(20*(page-1), min(20*page, listPhotoPreview.size)),
                prevKey = if (page > 1) page - 1 else null,
                nextKey = page + 1)
        }
        val res = kinopoiskRepository.getImagesForFilmPaging(id, page, type)
        return if (res != null) {
            LoadResult.Page(
                data = res.items,
                prevKey = page - 1,
                nextKey = if (res.items.isEmpty()) null else page + 1)
        } else {
            LoadResult.Error(Throwable("Error"))
        }
    }
}
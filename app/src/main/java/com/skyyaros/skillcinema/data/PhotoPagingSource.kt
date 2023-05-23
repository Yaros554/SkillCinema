package com.skyyaros.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyaros.skillcinema.entity.ImageItem

class PhotoPagingSource(
    private val kinopoiskRepository: KinopoiskRepository,
    private val id: Long,
    private val type: String,
    private val listPhotoPreview: List<ImageItem>
): PagingSource<Int, ImageItem>() {
    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int {
        return FIRST_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val page = params.key ?: FIRST_PAGE
        if (page == 1) {
            return LoadResult.Page(data = listPhotoPreview, prevKey = null, nextKey = page + 1)
        }
        val res = kinopoiskRepository.getImagesForFilmPaging(id, page, type)
        return if (res != null) {
            LoadResult.Page(data = res.items, prevKey = null, nextKey = if (res.items.isEmpty()) null else page + 1)
        } else {
            LoadResult.Error(Throwable("Error"))
        }
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}
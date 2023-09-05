package com.skyyaros.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.delay

class SearchPagingSource(
    private val kinopoiskRepository: KinopoiskRepository,
    private val searchQuery: SearchQuery
): PagingSource<Int, FilmPreview>() {
    override fun getRefreshKey(state: PagingState<Int, FilmPreview>): Int {
        return FIRST_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmPreview> {
        val page = params.key ?: FIRST_PAGE
        if (page == 1)
            delay(300)
        val res = kinopoiskRepository.getSearchFilms(searchQuery, page)
        return if (res != null)
            LoadResult.Page(data = res, prevKey = null, nextKey = if (res.isEmpty()) null else page + 1)
        else
            LoadResult.Error(Throwable("Error"))
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}
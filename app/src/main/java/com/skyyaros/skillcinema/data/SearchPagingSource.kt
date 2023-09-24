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
        val res = if (searchQuery.nameActor == null)
            kinopoiskRepository.getSearchFilms(searchQuery, page)
        else {
            kinopoiskRepository.getSearchActors(searchQuery.nameActor, page)?.map {
                FilmPreview(
                    it.kinopoiskId, null, it.posterUrl, it.nameRu, it.nameEn,
                    null, null, null, null, null, null
                )
            }
        }
        return if (res != null)
            LoadResult.Page(data = res, prevKey = null, nextKey = if (res.isEmpty()) null else page + 1)
        else
            LoadResult.Error(Throwable("Error"))
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}
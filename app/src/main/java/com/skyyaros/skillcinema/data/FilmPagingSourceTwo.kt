package com.skyyaros.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.FilmPreviewHalf

class FilmPagingSourceTwo(
    private val kinopoiskRepositoryDefault: KinopoiskRepositoryDefault,
    private val listFilmPreviewHalf: List<FilmPreviewHalf>
    ): PagingSource<Int, FilmPreview>() {
    override fun getRefreshKey(state: PagingState<Int, FilmPreview>): Int {
        return FIRST_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmPreview> {
        val page = params.key ?: FIRST_PAGE
        val res = kinopoiskRepositoryDefault.getFilmPreviewPage(listFilmPreviewHalf, page)
        return LoadResult.Page(data = res, prevKey = null, nextKey = if (res.isEmpty()) null else page + 1)
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}
package com.skyyaros.skillcinema.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyaros.skillcinema.entity.FilmPreview

class FilmPagingSource(
    private val kinopoiskRepositoryDefault: KinopoiskRepositoryDefault,
    private val mode: Int,
    private val countryId: Long? = null,
    private val genreId: Long? = null,
    private val listFilmPreview: List<FilmPreview>
    ): PagingSource<Int, FilmPreview>() {
    override fun getRefreshKey(state: PagingState<Int, FilmPreview>): Int {
        return FIRST_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmPreview> {
        val page = params.key ?: FIRST_PAGE
        if (page == 1) {
            return LoadResult.Page(data = listFilmPreview, prevKey = null, nextKey = page + 1)
        }
        if (mode == 2 || mode == 4) {
            val res = kinopoiskRepositoryDefault.getTopForHome(if (mode == 2) "TOP_100_POPULAR_FILMS" else "TOP_250_BEST_FILMS", page)
            return if (res != null) {
                LoadResult.Page(data = res, prevKey = null, nextKey = if (res.isEmpty()) null else page + 1)
            } else {
                LoadResult.Error(Throwable("Error"))
            }
        } else {
            val res = if (mode == 6) {
                kinopoiskRepositoryDefault.getFiltersOrSeriesForHome(null, null, page)
            } else {
                kinopoiskRepositoryDefault.getFiltersOrSeriesForHome(countryId!!, genreId!!, page)
            }
            return if (res != null) {
                LoadResult.Page(data = res, prevKey = null, nextKey = if (res.isEmpty()) null else page + 1)
            } else {
                LoadResult.Error(Throwable("Error"))
            }
        }
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}
package com.skyyaros.skillcinema.ui.listpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.FilmPagingSource
import com.skyyaros.skillcinema.data.FilmPagingSourceTwo
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.FilmPreviewHalf
import kotlinx.coroutines.flow.Flow

class ListpageViewModel(
    private val kinopoiskRepository: KinopoiskRepository,
    private val mode: Int,
    private val countryId: Long? = null,
    private val genreId: Long? = null,
    private val listFilmPreview: List<FilmPreview>?,
    private val listFilmPreviewHalf: List<FilmPreviewHalf>?
    ): ViewModel() {
    val pagedFilms: Flow<PagingData<FilmPreview>> = Pager(
        config = PagingConfig(pageSize = if (mode == 8 || mode == 7) 10 else 20),
        pagingSourceFactory = {
            if (mode == 8 || mode == 7)
                FilmPagingSourceTwo(kinopoiskRepository, listFilmPreviewHalf!!)
            else
                FilmPagingSource(kinopoiskRepository, mode, countryId, genreId, listFilmPreview!!)
        }
    ).flow.cachedIn(viewModelScope)
}
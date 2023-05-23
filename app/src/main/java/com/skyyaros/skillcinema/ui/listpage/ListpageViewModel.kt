package com.skyyaros.skillcinema.ui.listpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.FilmPagingSource
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.entity.FilmPreview
import kotlinx.coroutines.flow.Flow

class ListpageViewModel(
    private val kinopoiskRepository: KinopoiskRepository,
    private val mode: Int,
    private val countryId: Long? = null,
    private val genreId: Long? = null,
    private val listFilmPreview: List<FilmPreview>
    ): ViewModel() {
    val pagedFilms: Flow<PagingData<FilmPreview>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { FilmPagingSource(kinopoiskRepository, mode, countryId, genreId, listFilmPreview) }
    ).flow.cachedIn(viewModelScope)
}
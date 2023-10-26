package com.skyyaros.skillcinema.ui.filmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.FilmPagingSourceTwo
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.FilmPreviewHalf
import kotlinx.coroutines.flow.Flow

class FilmographyViewModel(
    listListFilmPreviewHalf: List<List<FilmPreviewHalf>>,
    kinopoiskRepositoryDefault: KinopoiskRepositoryDefault
): ViewModel() {
    var isInit = false
    val pagedFilms = mutableMapOf<String, Flow<PagingData<FilmPreview>>>()
    init {
        listListFilmPreviewHalf.forEach {
            pagedFilms += it.first().professionKey!! to Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = { FilmPagingSourceTwo(kinopoiskRepositoryDefault, it) }
            ).flow.cachedIn(viewModelScope)
        }
    }
}
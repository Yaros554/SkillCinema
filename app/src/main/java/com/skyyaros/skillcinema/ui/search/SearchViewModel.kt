package com.skyyaros.skillcinema.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.data.SearchPagingSource
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class SearchViewModel(
    private val kinopoiskRepositoryDefault: KinopoiskRepositoryDefault,
    initSearchQuery: SearchQuery,
    _filterList: List<FilmActorTable>
): ViewModel() {
    private var filterList = if (initSearchQuery.showViewedFilms) emptyList() else _filterList
    private val searchQueryFlow = MutableStateFlow(initSearchQuery)
    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = searchQueryFlow.asStateFlow().flatMapLatest {
        Pager(
            config = PagingConfig(pageSize = if (it.nameActor == null) 20 else 50),
            pagingSourceFactory = {
                SearchPagingSource(kinopoiskRepositoryDefault, it)
            }
        ).flow.map { pagingData ->
            pagingData.filter { filmPreview ->
                filterList.find { it.kinopoiskId == (filmPreview.kinopoiskId ?: filmPreview.filmId!!) } == null
            }
        }
    }.cachedIn(viewModelScope)

    fun createNewQuery(searchQuery: SearchQuery, curFilterList: List<FilmActorTable>) {
        filterList = if (searchQuery.showViewedFilms) emptyList() else curFilterList
        searchQueryFlow.value = searchQuery
    }
}
package com.skyyaros.skillcinema.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.SearchPagingSource
import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class SearchViewModel(private val kinopoiskRepository: KinopoiskRepository, initSearchQuery: SearchQuery): ViewModel() {
    private val searchQueryFlow = MutableStateFlow(initSearchQuery)
    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = searchQueryFlow.asStateFlow().flatMapLatest {
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                SearchPagingSource(kinopoiskRepository, it)
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun createNewQuery(searchQuery: SearchQuery) {
        searchQueryFlow.value = searchQuery
    }
}
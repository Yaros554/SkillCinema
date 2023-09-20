package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    var isFullPhotoFragment = false
    private val _resultsF = MutableSharedFlow<Boolean>()
    val resultF = _resultsF.asSharedFlow()
    private val _resultsV = MutableSharedFlow<Boolean>()
    val resultV = _resultsV.asSharedFlow()
    private val _resBackDialog = MutableSharedFlow<Int>()
    val resBackDialog = _resBackDialog.asSharedFlow()
    private val _resGenreCountry = MutableSharedFlow<Long>(1)
    val resGenreCountry = _resGenreCountry.asSharedFlow()
    private val _resYear = MutableSharedFlow<Int>(1)
    val resYear = _resYear.asSharedFlow()
    var searchQuery = SearchQuery(
        null, null, "YEAR", "ALL",
        0, 10, 1000, 3000,
        null
    )

    fun emitResultFV(mode: Int, isChecked: Boolean) {
        viewModelScope.launch {
            if (mode == 1)
                _resultsF.emit(isChecked)
            else
                _resultsV.emit(isChecked)
        }
    }

    fun emitResBackDialog(userSelect: Int) {
        viewModelScope.launch {
            _resBackDialog.emit(userSelect)
        }
    }

    fun emitGenreCountry(id: Long) {
        viewModelScope.launch {
            _resGenreCountry.emit(id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanGenreCountry() {
        _resGenreCountry.resetReplayCache()
    }

    fun emitYear(years: Int) {
        viewModelScope.launch {
            _resYear.emit(years)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanYear() {
        _resYear.resetReplayCache()
    }
}
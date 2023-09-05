package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.entity.SearchQuery
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
    var searchQuery = SearchQuery(
        null, null, "RATING", "ALL",
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
}
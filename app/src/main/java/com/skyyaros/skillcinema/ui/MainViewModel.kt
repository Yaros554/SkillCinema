package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    var isFullPhotoFragment = false
    private val _resultsF = MutableSharedFlow<Boolean>()
    val resultF = _resultsF.asSharedFlow()
    private val _resultsV = MutableSharedFlow<Boolean>()
    val resultV = _resultsV.asSharedFlow()

    fun emitResult(mode: Int, isChecked: Boolean) {
        viewModelScope.launch {
            if (mode == 1)
                _resultsF.emit(isChecked)
            else
                _resultsV.emit(isChecked)
        }
    }
}
package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FullscreenDialogInfoViewModel: ViewModel() {
    private val _resultsF = MutableSharedFlow<Boolean>(1)
    val resultF = _resultsF.asSharedFlow()
    private val _resultsV = MutableSharedFlow<Boolean>(1)
    val resultV = _resultsV.asSharedFlow()

    fun emitResultFV(mode: FullscreenDialogInfoMode, isChecked: Boolean) {
        viewModelScope.launch {
            if (mode == FullscreenDialogInfoMode.PHOTO)
                _resultsF.emit(isChecked)
            else
                _resultsV.emit(isChecked)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearResultFV(mode: FullscreenDialogInfoMode) {
        viewModelScope.launch {
            if (mode == FullscreenDialogInfoMode.PHOTO)
                _resultsF.resetReplayCache()
            else
                _resultsV.resetReplayCache()
        }
    }
}
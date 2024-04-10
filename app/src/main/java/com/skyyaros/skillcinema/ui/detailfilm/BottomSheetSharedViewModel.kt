package com.skyyaros.skillcinema.ui.detailfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BottomSheetSharedViewModel: ViewModel() {
    private val _resBottomSh = MutableSharedFlow<Boolean>(1)
    val resBottomSh = _resBottomSh.asSharedFlow()

    fun emitBottomSh() {
        viewModelScope.launch {
            _resBottomSh.emit(true)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanBottomSh() {
        _resBottomSh.resetReplayCache()
    }
}
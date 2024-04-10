package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.entity.AppTheme
import com.skyyaros.skillcinema.entity.VideoSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BackDialogViewModel: ViewModel() {
    private val _resBackDialog = MutableSharedFlow<BackDialogResult>(1)
    val resBackDialog = _resBackDialog.asSharedFlow()

    fun emitResBackDialog(res: BackDialogResult) {
        viewModelScope.launch {
            _resBackDialog.emit(res)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanResBackDialog() {
        _resBackDialog.resetReplayCache()
    }

    lateinit var curSource: VideoSource
    lateinit var curTheme: AppTheme
    var animActive = true
}
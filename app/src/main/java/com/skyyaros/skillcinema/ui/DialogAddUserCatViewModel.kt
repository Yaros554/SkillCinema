package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DialogAddUserCatViewModel: ViewModel() {
    private val _resAddNewCat = MutableSharedFlow<String>(1)
    val resAddNewCat = _resAddNewCat.asSharedFlow()

    fun emitNewCat(newCat: String) {
        viewModelScope.launch {
            _resAddNewCat.emit(newCat)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanNewCat() {
        _resAddNewCat.resetReplayCache()
    }
}
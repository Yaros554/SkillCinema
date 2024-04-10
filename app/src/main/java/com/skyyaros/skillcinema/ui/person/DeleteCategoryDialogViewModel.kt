package com.skyyaros.skillcinema.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DeleteCategoryDialogViewModel: ViewModel() {
    private val _resDeleteCat = MutableSharedFlow<String>(1)
    val resDeleteCat = _resDeleteCat.asSharedFlow()

    fun emitDeleteCat(category: String) {
        viewModelScope.launch {
            _resDeleteCat.emit(category)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanDeleteCat() {
        _resDeleteCat.resetReplayCache()
    }
}
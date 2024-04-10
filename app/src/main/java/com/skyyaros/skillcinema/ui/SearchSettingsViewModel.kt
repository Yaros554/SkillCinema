package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.ui.dopsearch.SearchSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchSettingsViewModel: ViewModel() {
    private val _SearchSettingsFlow = MutableSharedFlow<SearchSettings>(1)
    val SearchSettingsFlow = _SearchSettingsFlow.asSharedFlow()

    fun emitSearchSettings(newSetting: SearchSettings) {
        viewModelScope.launch {
            _SearchSettingsFlow.emit(newSetting)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanSearchSettings() {
        viewModelScope.launch {
            _SearchSettingsFlow.resetReplayCache()
        }
    }
}
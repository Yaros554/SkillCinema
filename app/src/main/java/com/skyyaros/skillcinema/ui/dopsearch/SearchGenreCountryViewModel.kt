package com.skyyaros.skillcinema.ui.dopsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchGenreCountryViewModel(mode: Int, kinopoiskRepositoryDefault: KinopoiskRepositoryDefault): ViewModel() {
    private val data = if (mode == 1)
        kinopoiskRepositoryDefault.listGenres!!.map { GenreOrCountry(it.id, it.genre) }.sortedBy { it.text }
    else
        kinopoiskRepositoryDefault.listCountries!!.map { GenreOrCountry(it.id, it.country) }.sortedBy { it.text }
    private val _genreCountryFlow = MutableStateFlow(data)
    val genreCountryFlow = _genreCountryFlow.asStateFlow()
    var isInit = true

    fun emitData(text: String) {
        viewModelScope.launch {
            _genreCountryFlow.emit(data.filter { it.text.contains(text, true) })
        }
    }
}
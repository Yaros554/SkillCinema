package com.skyyaros.skillcinema.ui.search

import androidx.lifecycle.ViewModel
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.entity.SearchQuery

class SetSearchViewModel(oldQuery: SearchQuery, private val kinopoiskRepository: KinopoiskRepository): ViewModel() {
    var type = oldQuery.type
    var country = oldQuery.countries
    var genre = oldQuery.genres
    var yearFrom = oldQuery.yearFrom
    var yearTo = oldQuery.yearTo
    var ratingFrom = oldQuery.ratingFrom
    var ratingTo = oldQuery.ratingTo
    var order = oldQuery.order
    var nameActor = oldQuery.nameActor
    val countries get() = kinopoiskRepository.listCountries
    val genres get() = kinopoiskRepository.listGenres
}
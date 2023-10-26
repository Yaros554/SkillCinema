package com.skyyaros.skillcinema.ui.search

import androidx.lifecycle.ViewModel
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.entity.SearchQuery

class SetSearchViewModel(oldQuery: SearchQuery, private val kinopoiskRepositoryDefault: KinopoiskRepositoryDefault): ViewModel() {
    var type = oldQuery.type
    var country = oldQuery.countries
    var genre = oldQuery.genres
    var yearFrom = oldQuery.yearFrom
    var yearTo = oldQuery.yearTo
    var ratingFrom = oldQuery.ratingFrom
    var ratingTo = oldQuery.ratingTo
    var order = oldQuery.order
    var nameActor = oldQuery.nameActor
    var showViewedFilms = oldQuery.showViewedFilms
    val countries get() = kinopoiskRepositoryDefault.listCountries
    val genres get() = kinopoiskRepositoryDefault.listGenres
}
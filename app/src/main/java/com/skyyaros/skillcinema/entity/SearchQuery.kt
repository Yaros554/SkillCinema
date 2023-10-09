package com.skyyaros.skillcinema.entity

data class SearchQuery(
    val countries: Long?=null, val genres: Long?=null,
    val order: String="YEAR", val type: String="ALL",
    val ratingFrom: Int=0, val ratingTo: Int=10,
    val yearFrom: Int=1000, val yearTo: Int=3000,
    val keyword: String?=null, val nameActor: String? = null,
    val showViewedFilms: Boolean = true
)

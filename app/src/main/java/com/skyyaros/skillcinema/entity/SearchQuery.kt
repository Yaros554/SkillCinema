package com.skyyaros.skillcinema.entity

data class SearchQuery(
    val countries: Long?, val genres: Long?,
    val order: String, val type: String,
    val ratingFrom: Int, val ratingTo: Int,
    val yearFrom: Int, val yearTo: Int,
    val keyword: String?
)

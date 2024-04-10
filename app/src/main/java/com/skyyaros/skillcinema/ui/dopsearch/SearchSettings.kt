package com.skyyaros.skillcinema.ui.dopsearch

data class SearchSettings(
    val countryId: Long = -1L,
    val genreId: Long = -1L,
    val yearFrom: Int = -1,
    val yearTo: Int = -1,
    val type: TypeSettings
)

enum class TypeSettings {
    COUNTRY, GENRE, YEAR
}

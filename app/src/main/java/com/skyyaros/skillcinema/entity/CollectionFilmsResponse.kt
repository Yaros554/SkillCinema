package com.skyyaros.skillcinema.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class PremieresResponse(
    @Json(name = "total") val count: Int,
    @Json(name = "items") val usefulData: List<FilmPreview>
)

@JsonClass(generateAdapter = true)
data class TopResponse(
    @Json(name = "pagesCount") val pagesCount: Int,
    @Json(name = "films") val usefulData: List<FilmPreview>
)

@JsonClass(generateAdapter = true)
data class FiltersOrSeriesResponse(
    @Json(name = "total") val count: Int,
    @Json(name = "totalPages") val pagesCount: Int,
    @Json(name = "items") val usefulData: List<FilmPreview>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class FilmPreview(
    @Json(name = "kinopoiskId") val kinopoiskId: Long?,
    @Json(name = "filmId") val filmId: Long?,
    @Json(name = "posterUrlPreview") val imageUrl: String?,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "nameOriginal") val nameOriginal: String?,
    @Json(name = "genres") val genres: List<Genre>?,
    @Json(name = "rating") val rating: String?,
    @Json(name = "ratingKinopoisk") val ratingKinopoisk: String?,
    @Json(name = "premiereRu") val premiereRu: String?,
    @Json(name = "professionKey") val professionKey: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "genre") val genre: String
) : Parcelable

@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "country") val country: String
)

data class FilmsPreviewWithData(
    val films: List<List<FilmPreview>?>,
    val countryId1: Long,
    val countryName1: String,
    val genreId1: Long,
    var genreName1: String,
    var countryId2: Long,
    var countryName2: String,
    var genreId2: Long,
    var genreName2: String
)

@JsonClass(generateAdapter = true)
data class GenresAndCountriesResponse(
    @Json(name = "genres") val genres: List<GenreForFilter>,
    @Json(name = "countries") val countries: List<CountryForFilter>
)

@JsonClass(generateAdapter = true)
data class GenreForFilter(
    @Json(name = "id") val id: Long,
    @Json(name = "genre") val genre: String
)

@JsonClass(generateAdapter = true)
data class CountryForFilter(
    @Json(name = "id") val id: Long,
    @Json(name = "country") val country: String
)

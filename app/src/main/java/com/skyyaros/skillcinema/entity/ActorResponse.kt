package com.skyyaros.skillcinema.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class DetailActor(
    @Json(name = "personId") val personId: Long,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "posterUrl") val posterUrl: String,
    @Json(name = "birthday") val birthday: String?,
    @Json(name = "death") val death: String?,
    @Json(name = "age") val age: Int?,
    @Json(name = "birthplace") val birthplace: String?,
    @Json(name = "deathplace") val deathplace: String?,
    @Json(name = "hasAwards")val hasAwards: Int?,
    @Json(name = "profession") val profession: String?,
    @Json(name = "facts") val facts: List<String>?,
    @Json(name = "films") val listFilmPreviewHalf: List<FilmPreviewHalf>?,
    var listBestFilmPreviewHalf: List<FilmPreviewHalf>?,
    var best10Films: List<FilmPreview>?
)

@Parcelize
@JsonClass(generateAdapter = true)
data class FilmPreviewHalf(
    @Json(name = "filmId") val filmId: Long,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "professionKey") val professionKey: String?,
    @Json(name = "rating") val rating: String?,
    @Json(name = "posterUrlPreview") val imageUrl: String?
): Parcelable

@JsonClass(generateAdapter = true)
data class ListSearchActorResponse(
    @Json(name = "items") val items: List<SearchActor>
)
@JsonClass(generateAdapter = true)
data class SearchActor(
    @Json(name = "kinopoiskId") val kinopoiskId: Long,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "posterUrl") val posterUrl: String
)
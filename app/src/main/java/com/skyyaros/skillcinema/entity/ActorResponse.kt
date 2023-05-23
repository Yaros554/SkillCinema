package com.skyyaros.skillcinema.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    @Json(name = "films") val films: List<FilmPreview>?
)

@JsonClass(generateAdapter = true)
data class DopInfoForFilm(
    @Json(name = "posterUrlPreview") val posterUrlPreview: String,
    @Json(name = "year") val year: Int?,
    @Json(name = "genres") val genres: List<Genre>
)
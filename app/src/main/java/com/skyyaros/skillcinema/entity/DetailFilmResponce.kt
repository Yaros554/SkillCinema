package com.skyyaros.skillcinema.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class DetailFilm(
    @Json(name = "kinopoiskId") val kinopoiskId: Long,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "nameOriginal") val nameOriginal: String?,
    @Json(name = "posterUrl") val posterUrl: String,
    @Json(name = "coverUrl") val coverUrl: String?,
    @Json(name = "logoUrl") val logoUrl: String?,
    @Json(name = "ratingKinopoisk") val ratingKinopoisk: String?,
    @Json(name = "year") val year: Int?,
    @Json(name = "genres") val genres: List<Genre>,
    @Json(name = "countries") val countries: List<Country>,
    @Json(name = "filmLength") val filmLength: Int?,
    @Json(name = "ratingAgeLimits") val ratingAgeLimits: String?,
    @Json(name = "shortDescription") val shortDescription: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "webUrl") val webUrl: String,
    @Json(name = "type") val type: String
)

@Parcelize
@JsonClass(generateAdapter = true)
data class ActorPreview(
    @Json(name = "staffId") val staffId: Long,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "posterUrl") val posterUrl: String,
    @Json(name = "professionText") val professionText: String,
    @Json(name = "professionKey") val professionKey: String,
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ImageResponse(
    @Json(name = "total") val total: Int,
    @Json(name = "items") val items: List<ImageItem>,
    var imageType: String? = null
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ImageItem(
    @Json(name = "imageUrl") val imageUrl: String,
    @Json(name = "previewUrl") val previewUrl: String
): Parcelable

@JsonClass(generateAdapter = true)
data class SimilarFilmsResponse(
    @Json(name = "items") val items: List<FilmPreview>
)

@JsonClass(generateAdapter = true)
data class SeasonResponse(
    @Json(name = "items") val items: List<Season>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Season(
    @Json(name = "number") val number: Int,
    @Json(name = "episodes") val episodes: List<Episode>
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Episode(
    @Json(name = "seasonNumber") val seasonNumber: Int,
    @Json(name = "episodeNumber") val episodeNumber: Int,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "synopsis") val synopsis: String?,
    @Json(name = "releaseDate") val releaseDate: String?
): Parcelable

data class FullDetailFilm(
    val detailFilm: DetailFilm,
    val actors: List<ActorPreview>?,
    val images: List<ImageResponse>?,
    val similar: List<FilmPreview>?,
    val series: List<Season>?
)
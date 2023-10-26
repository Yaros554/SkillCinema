package com.skyyaros.skillcinema.data

import androidx.annotation.VisibleForTesting
import com.skyyaros.skillcinema.entity.DetailActor
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.FilmPreviewHalf
import com.skyyaros.skillcinema.entity.FilmsPreviewWithData
import com.skyyaros.skillcinema.entity.FullDetailFilm
import com.skyyaros.skillcinema.entity.GenresAndCountriesResponse
import com.skyyaros.skillcinema.entity.ImageResponse
import com.skyyaros.skillcinema.entity.SearchActor
import com.skyyaros.skillcinema.entity.SearchQuery
import retrofit2.Response

interface KinopoiskRepository {
    suspend fun getFilmsForHome(): FilmsPreviewWithData?

    @VisibleForTesting
    suspend fun getGenresAndCountries(): Response<GenresAndCountriesResponse>

    suspend fun getTopForHome(type: String, page: Int = 1): List<FilmPreview>?

    suspend fun getFiltersOrSeriesForHome(countries: Long? = null, genres: Long? = null, page: Int = 1): List<FilmPreview>?

    suspend fun getFullDetailFilm(id: Long): FullDetailFilm?

    suspend fun getActorDetail(id: Long): DetailActor?

    suspend fun getFilmPreviewPage(listFilmPreviewHalf: List<FilmPreviewHalf>, page: Int): List<FilmPreview>

    suspend fun getImagesForFilmPaging(id: Long, page: Int, type: String): ImageResponse?

    suspend fun getSearchFilms(searchQuery: SearchQuery, page: Int): List<FilmPreview>?

    suspend fun getSearchActors(name: String, page: Int): List<SearchActor>?
}
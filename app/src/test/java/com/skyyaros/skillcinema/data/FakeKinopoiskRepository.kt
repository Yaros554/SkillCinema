package com.skyyaros.skillcinema.data

import com.google.gson.Gson
import com.skyyaros.skillcinema.entity.DetailActor
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.FilmPreviewHalf
import com.skyyaros.skillcinema.entity.FilmsPreviewWithData
import com.skyyaros.skillcinema.entity.FullDetailFilm
import com.skyyaros.skillcinema.entity.Genre
import com.skyyaros.skillcinema.entity.GenresAndCountriesResponse
import com.skyyaros.skillcinema.entity.ImageResponse
import com.skyyaros.skillcinema.entity.SearchActor
import com.skyyaros.skillcinema.entity.SearchQuery
import retrofit2.Response
import java.io.File

class FakeKinopoiskRepository: KinopoiskRepository {
    var isError = false
    var needUpdate = false
    override suspend fun getFilmsForHome(): FilmsPreviewWithData? {
        return if (isError)
            null
        else {
            val myGson = if (needUpdate)
                File("testData/TestDataHomeFragment2.txt").readText()
            else
                File("testData/TestDataHomeFragment.txt").readText()
            Gson().fromJson(myGson, FilmsPreviewWithData::class.java)
        }
    }

    override suspend fun getGenresAndCountries(): Response<GenresAndCountriesResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getTopForHome(type: String, page: Int): List<FilmPreview>? {
        TODO("Not yet implemented")
    }

    override suspend fun getFiltersOrSeriesForHome(
        countries: Long?,
        genres: Long?,
        page: Int
    ): List<FilmPreview>? {
        TODO("Not yet implemented")
    }

    override suspend fun getFullDetailFilm(id: Long): FullDetailFilm? {
        TODO("Not yet implemented")
    }

    override suspend fun getActorDetail(id: Long): DetailActor? {
        TODO("Not yet implemented")
    }

    override suspend fun getFilmPreviewPage(
        listFilmPreviewHalf: List<FilmPreviewHalf>,
        page: Int
    ): List<FilmPreview> {
        TODO("Not yet implemented")
    }

    override suspend fun getImagesForFilmPaging(id: Long, page: Int, type: String): ImageResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchFilms(searchQuery: SearchQuery, page: Int): List<FilmPreview>? {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchActors(name: String, page: Int): List<SearchActor>? {
        TODO("Not yet implemented")
    }
}
package com.skyyaros.skillcinema.data

import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.skyyaros.skillcinema.entity.DetailActor
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.FilmPreviewHalf
import com.skyyaros.skillcinema.entity.FilmsPreviewWithData
import com.skyyaros.skillcinema.entity.FullDetailFilm
import com.skyyaros.skillcinema.entity.GenresAndCountriesResponse
import com.skyyaros.skillcinema.entity.ImageResponse
import com.skyyaros.skillcinema.entity.SearchActor
import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class FakeKinopoiskRepository: KinopoiskRepository {
    var isError = false
    var needUpdate = false
    override suspend fun getFilmsForHome(): FilmsPreviewWithData? {
        return if (isError)
            null
        else {
            val input = InstrumentationRegistry.getInstrumentation().targetContext.openFileInput(
                if (needUpdate) "TestDataHomeFragment2.txt" else "TestDataHomeFragment.txt"
            )
            val myGson = withContext(Dispatchers.IO) {
                val bytes = ByteArray(input.available())
                input.read(bytes)
                input.close()
                String(bytes)
            }
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
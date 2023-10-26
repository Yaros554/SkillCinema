package com.skyyaros.skillcinema.data

import com.skyyaros.skillcinema.entity.ActorPreview
import com.skyyaros.skillcinema.entity.CountryForFilter
import com.skyyaros.skillcinema.entity.DetailActor
import com.skyyaros.skillcinema.entity.DetailFilm
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.entity.GenreForFilter
import com.skyyaros.skillcinema.entity.GenresAndCountriesResponse
import com.skyyaros.skillcinema.entity.ImageResponse
import com.skyyaros.skillcinema.entity.ListFilmPreviewResponse
import com.skyyaros.skillcinema.entity.ListSearchActorResponse
import com.skyyaros.skillcinema.entity.MoneyResponse
import com.skyyaros.skillcinema.entity.SeasonResponse
import com.skyyaros.skillcinema.entity.SimilarFilmsResponse
import com.skyyaros.skillcinema.entity.VideoResponse
import okhttp3.ResponseBody
import retrofit2.Response

class FakeKinopoiskApi: KinopoiskApi {
    private var errorInfo = ErrorInfo(200)

    fun setError(code: Int, count: Int = 1) {
        errorInfo = ErrorInfo(code, count)
    }
    override suspend fun getPremieres(year: Int, month: String, header: Map<String, String>): Response<ListFilmPreviewResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getTop(type: String, page: Int, header: Map<String, String>): Response<ListFilmPreviewResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getFiltersOrSeries(
        type: String,
        countries: Long?,
        genres: Long?,
        page: Int,
        header: Map<String, String>
    ): Response<ListFilmPreviewResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getGenresAndCountries(header: Map<String, String>): Response<GenresAndCountriesResponse> {
        return if (errorInfo.count == 0 || errorInfo.code == 200) {
            Response.success(200, etalon)
        } else {
            errorInfo.count--
            Response.error(errorInfo.code, ResponseBody.create(null, ""))
        }
    }

    override suspend fun getFilmDetail(id: Long, header: Map<String, String>): Response<DetailFilm> {
        TODO("Not yet implemented")
    }

    override suspend fun getActorsInFilm(id: Long, header: Map<String, String>): Response<List<ActorPreview>> {
        TODO("Not yet implemented")
    }

    override suspend fun getImagesInFilm(
        id: Long,
        type: String,
        page: Int,
        header: Map<String, String>
    ): Response<ImageResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getVideoInFilm(id: Long, header: Map<String, String>): Response<VideoResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSimilarFilms(id: Long, header: Map<String, String>): Response<SimilarFilmsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSeasons(id: Long, header: Map<String, String>): Response<SeasonResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getActorDetail(id: Long, header: Map<String, String>): Response<DetailActor> {
        TODO("Not yet implemented")
    }

    override suspend fun getFilmPreview(id: Long, header: Map<String, String>): Response<FilmPreview> {
        TODO("Not yet implemented")
    }

    override suspend fun getMoney(id: Long, header: Map<String, String>): Response<MoneyResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchFilms(
        countries: Long?,
        genres: Long?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String?,
        page: Int,
        header: Map<String, String>
    ): Response<ListFilmPreviewResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchActors(name: String, page: Int, header: Map<String, String>): Response<ListSearchActorResponse> {
        TODO("Not yet implemented")
    }

    companion object {
        val etalon = GenresAndCountriesResponse(
            listOf(
                GenreForFilter(1, "Боевик"), GenreForFilter(2, "Фэнтези")
            ),
            listOf(
                CountryForFilter(1, "Русь"), CountryForFilter(2, "Зототая Орда")
            )
        )
    }

    data class ErrorInfo(val code: Int, var count: Int = 1)
}
package com.skyyaros.skillcinema.data

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.skyyaros.skillcinema.BuildConfig
import com.skyyaros.skillcinema.entity.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*

interface KinopoiskApi {
    @GET("api/v2.2/films/premieres")
    suspend fun getPremieres(@Query("year") year: Int, @Query("month") month: String, @HeaderMap header: Map<String, String>): Response<ListFilmPreviewResponse>

    @GET("api/v2.2/films/top")
    suspend fun getTop(@Query("type") type: String, @Query("page") page: Int, @HeaderMap header: Map<String, String>): Response<ListFilmPreviewResponse>

    @GET("api/v2.2/films")
    suspend fun getFiltersOrSeries(
        @Query("type") type: String,
        @Query("countries") countries: Long?,
        @Query("genres") genres: Long?,
        @Query("page") page: Int,
        @HeaderMap header: Map<String, String>
    ): Response<ListFilmPreviewResponse>

    @GET("api/v2.2/films/filters")
    suspend fun getGenresAndCountries(@HeaderMap header: Map<String, String>): Response<GenresAndCountriesResponse>

    @GET("api/v2.2/films/{id}")
    suspend fun getFilmDetail(@Path("id") id: Long, @HeaderMap header: Map<String, String>): Response<DetailFilm>

    @GET("api/v1/staff")
    suspend fun getActorsInFilm(@Query("filmId") id: Long, @HeaderMap header: Map<String, String>): Response<List<ActorPreview>>

    @GET("api/v2.2/films/{id}/images")
    suspend fun getImagesInFilm(
        @Path("id") id: Long,
        @Query("type") type: String,
        @Query("page") page: Int,
        @HeaderMap header: Map<String, String>
    ): Response<ImageResponse>

    @GET("api/v2.2/films/{id}/videos")
    suspend fun getVideoInFilm(
        @Path("id") id: Long,
        @HeaderMap header: Map<String, String>
    ): Response<VideoResponse>

    @GET("api/v2.2/films/{id}/similars")
    suspend fun getSimilarFilms(@Path("id") id: Long, @HeaderMap header: Map<String, String>): Response<SimilarFilmsResponse>

    @GET("api/v2.2/films/{id}/seasons")
    suspend fun getSeasons(@Path("id") id: Long, @HeaderMap header: Map<String, String>): Response<SeasonResponse>

    @GET("api/v1/staff/{id}")
    suspend fun getActorDetail(@Path("id") id: Long, @HeaderMap header: Map<String, String>): Response<DetailActor>

    @GET("api/v2.2/films/{id}")
    suspend fun getFilmPreview(@Path("id") id: Long, @HeaderMap header: Map<String, String>): Response<FilmPreview>

    @GET("api/v2.2/films/{id}/box_office")
    suspend fun getMoney(@Path("id") id: Long, @HeaderMap header: Map<String, String>): Response<MoneyResponse>

    @GET("api/v2.2/films")
    suspend fun getSearchFilms(
        @Query("countries") countries: Long?,
        @Query("genres") genres: Long?,
        @Query("order") order: String,
        @Query("type") type: String,
        @Query("ratingFrom") ratingFrom: Int,
        @Query("ratingTo") ratingTo: Int,
        @Query("yearFrom") yearFrom: Int,
        @Query("yearTo") yearTo: Int,
        @Query("keyword") keyword: String?,
        @Query("page") page: Int,
        @HeaderMap header: Map<String, String>
    ): Response<ListFilmPreviewResponse>

    @GET("api/v1/persons")
    suspend fun getSearchActors(
        @Query("name") name: String,
        @Query("page") page: Int,
        @HeaderMap header: Map<String, String>
    ):Response<ListSearchActorResponse>

    companion object RetrofitProvider {
        private val mutex = Mutex()
        @VisibleForTesting
        val keys = (BuildConfig.API_KEYS).toList()
        private var keyIndex = 0
        private const val BASE_URL = "https://kinopoiskapiunofficial.tech/"

        fun provide(): KinopoiskApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(KinopoiskApi::class.java)
        }

        suspend fun getCurrentKey(): Map<String, String> = mutex.withLock { mapOf("X-API-KEY" to keys[keyIndex]) }

        suspend fun getNewKey(oldKey: Map<String, String>): Map<String, String> {
            return mutex.withLock {
                if (oldKey["X-API-KEY"] == keys[keyIndex]) {
                    keyIndex = (keyIndex + 1) % keys.size
                }
                mapOf("X-API-KEY" to keys[keyIndex])
            }
        }

        fun getKeyBaseSize(): Int = keys.size
    }
}
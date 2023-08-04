package com.skyyaros.skillcinema.data

import android.util.Log
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
    suspend fun getPremieres(@Query("year") year: Int, @Query("month") month: String, @HeaderMap header: Map<String, String>): Response<PremieresResponse>

    @GET("api/v2.2/films/top")
    suspend fun getTop(@Query("type") type: String, @Query("page") page: Int, @HeaderMap header: Map<String, String>): Response<TopResponse>

    @GET("api/v2.2/films")
    suspend fun getFiltersOrSeries(
        @Query("type") type: String,
        @Query("countries") countries: Long?,
        @Query("genres") genres: Long?,
        @Query("page") page: Int,
        @HeaderMap header: Map<String, String>
    ): Response<FiltersOrSeriesResponse>

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

    companion object RetrofitProvider {
        private val mutex = Mutex()
        private val keys = listOf(
            "d3bbd027-4688-4e96-ad5d-f087a9ed84e8",
            "d6e4602b-caca-4926-9973-72d549051524",
            "5fbdaf0f-8e4d-4d4d-9874-0ff9230f5048"
        )
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
                    Log.d("ChangeKey", "Change key")
                    keyIndex = (keyIndex + 1) % keys.size
                }
                mapOf("X-API-KEY" to keys[keyIndex])
            }
        }

        fun getKeyBaseSize(): Int = keys.size
    }
}
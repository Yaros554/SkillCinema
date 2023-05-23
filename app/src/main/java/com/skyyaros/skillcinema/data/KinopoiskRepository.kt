package com.skyyaros.skillcinema.data

import com.skyyaros.skillcinema.entity.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.*

class KinopoiskRepository(private val kinopoiskApi: KinopoiskApi) {
    suspend fun getFilmsForHome(): FilmsPreviewWithData? = coroutineScope {
        var dynamicFilms1: List<FilmPreview>? = null
        var dynamicFilms2: List<FilmPreview>? = null
        var countryId1 = -1L
        var countryName1 = ""
        var genreId1 = -1L
        var genreName1 = ""
        var countryId2 = -1L
        var countryName2 = ""
        var genreId2 = -1L
        var genreName2 = ""
        try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var genresAndCountriesResponse = kinopoiskApi.getGenresAndCountries(apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((genresAndCountriesResponse.code() == 402 || genresAndCountriesResponse.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                genresAndCountriesResponse = kinopoiskApi.getGenresAndCountries(apiKey)
                count++
            }
            if (genresAndCountriesResponse.isSuccessful) {
                val listGenres = genresAndCountriesResponse.body()!!.genres
                val listCountries = genresAndCountriesResponse.body()!!.countries
                var needFilms = true
                while (needFilms) {
                    val indexCountry = kotlin.random.Random.nextInt(0, listCountries.size)
                    val indexGenre = kotlin.random.Random.nextInt(0, listGenres.size)
                    countryId1 = listCountries[indexCountry].id
                    genreId1 = listGenres[indexGenre].id
                    val listFilms = getFiltersOrSeriesForHome(countryId1, genreId1)
                    if (listFilms == null) {
                        needFilms = false
                    } else if (listFilms.size > 5) {
                        countryName1 = listCountries[indexCountry].country
                        genreName1 = listGenres[indexGenre].genre
                        dynamicFilms1 = listFilms
                        needFilms = false
                    }
                }
                if (dynamicFilms1 != null) {
                    needFilms = true
                    while (needFilms) {
                        var indexCountry: Int
                        var indexGenre: Int
                        do {
                            indexCountry = kotlin.random.Random.nextInt(0, listCountries.size)
                            indexGenre = kotlin.random.Random.nextInt(0, listGenres.size)
                            countryId2 = listCountries[indexCountry].id
                            genreId2 = listGenres[indexGenre].id
                        } while (countryId2 == countryId1 && genreId2 == genreId1)
                        val listFilms = getFiltersOrSeriesForHome(countryId2, genreId2)
                        if (listFilms == null) {
                            needFilms = false
                        } else if (listFilms.size > 5) {
                            countryName2 = listCountries[indexCountry].country
                            genreName2 = listGenres[indexGenre].genre
                            dynamicFilms2 = listFilms
                            needFilms = false
                        }
                    }
                }
            }
        } catch (_: Throwable) { }

        val deferreds: List<Deferred<List<FilmPreview>?>> = listOf(
            async { getPremieresForHome() },
            async { getTopForHome("TOP_100_POPULAR_FILMS") },
            async { getTopForHome("TOP_250_BEST_FILMS") },
            async { getFiltersOrSeriesForHome() }
        )
        val result: MutableList<List<FilmPreview>?> = deferreds.awaitAll() as MutableList<List<FilmPreview>?>
        result.add(dynamicFilms1)
        result.add(dynamicFilms2)
        if (!result.all { it == null }) {
            FilmsPreviewWithData(
                result,
                countryId1, countryName1,
                genreId1, genreName1,
                countryId2, countryName2,
                genreId2, genreName2
            )
        } else {
            null
        }
    }

    private suspend fun getPremieresForHome(): List<FilmPreview>? {
        val monthName = listOf(
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"
        )
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val curTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = curTimeMillis
        val year = calendar.get(Calendar.YEAR)
        val month = monthName[calendar.get(Calendar.MONTH)]
        val curTimeMillisTwoWeek = curTimeMillis + 14*24*60*60*1000
        calendar.timeInMillis = curTimeMillisTwoWeek
        val yearTwoWeek = calendar.get(Calendar.YEAR)
        val monthTwoWeek = monthName[calendar.get(Calendar.MONTH)]
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getPremieres(year, month, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getPremieres(year, month, apiKey)
                count++
            }
            if (res.isSuccessful) {
                val films = res.body()!!.usefulData.filter {
                    val filmTimeMillis = formatter.parse(it.premiereRu!!)!!.time
                    filmTimeMillis in curTimeMillis..curTimeMillisTwoWeek
                }
                if (month != monthTwoWeek) {
                    var res2 = kinopoiskApi.getPremieres(yearTwoWeek, monthTwoWeek, apiKey)
                    count = 1
                    while ((res2.code() == 402 || res2.code() == 429) && count < keysDatabaseSize) {
                        apiKey = KinopoiskApi.getNewKey(apiKey)
                        res2 = kinopoiskApi.getPremieres(yearTwoWeek, monthTwoWeek, apiKey)
                        count++
                    }
                    if (res2.isSuccessful) {
                        val films2 = res2.body()!!.usefulData.filter {
                            val filmTimeMillis = formatter.parse(it.premiereRu!!)!!.time
                            filmTimeMillis in curTimeMillis..curTimeMillisTwoWeek
                        }
                        (films + films2).sortedBy { formatter.parse(it.premiereRu!!)!!.time }
                    } else {
                        films
                    }
                } else {
                    films
                }
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    suspend fun getTopForHome(type: String, page: Int = 1): List<FilmPreview>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getTop(type, page, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getTop(type, page, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!.usefulData
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    suspend fun getFiltersOrSeriesForHome(countries: Long? = null, genres: Long? = null, page: Int = 1): List<FilmPreview>? {
        return try {
            if (countries == -1L || genres == -1L) {
                return null
            }
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = if (countries == null)
                kinopoiskApi.getFiltersOrSeries("TV_SERIES", null, null, page, apiKey)
            else
                kinopoiskApi.getFiltersOrSeries("FILM", countries, genres, page, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = if (countries == null)
                    kinopoiskApi.getFiltersOrSeries("TV_SERIES", null, null, page, apiKey)
                else
                    kinopoiskApi.getFiltersOrSeries("FILM", countries, genres, page, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!.usefulData
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    suspend fun getFullDetailFilm(id: Long): FullDetailFilm? {
        val detailFilm = getDetailFilm(id)
        return if (detailFilm != null) {
            val actors = getActorsInFilm(id)
            val images = getImagesInFilm(id)
            val similars = getSimilarFilms(id)
            val seasons = if (detailFilm.type == "TV_SERIES" || detailFilm.type == "MINI_SERIES" || detailFilm.type == "TV_SHOW")
                getSeasons(id)
            else
                null
            FullDetailFilm(detailFilm, actors, images, similars, seasons)
        } else {
            null
        }
    }

    private suspend fun getDetailFilm(id: Long): DetailFilm? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getFilmDetail(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getFilmDetail(id, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    private suspend fun getActorsInFilm(id: Long): List<ActorPreview>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getActorsInFilm(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getActorsInFilm(id, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    private suspend fun getImagesInFilm(id: Long): List<ImageResponse>? = coroutineScope {
        val types = listOf(
            "STILL", "SHOOTING", "POSTER", "FAN_ART", "PROMO",
            "CONCEPT", "WALLPAPER", "COVER", "SCREENSHOT"
        )
        return@coroutineScope try {
            val deferreds = types.map {
                async {
                    var apiKey = KinopoiskApi.getCurrentKey()
                    var res = kinopoiskApi.getImagesInFilm(id, it, 1, apiKey)
                    var count = 1
                    val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
                    while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                        apiKey = KinopoiskApi.getNewKey(apiKey)
                        res = kinopoiskApi.getImagesInFilm(id, it, 1, apiKey)
                        count++
                    }
                    if (res.isSuccessful) {
                        res.body()!!
                    } else {
                        null
                    }
                }
            }
            val prevRes = deferreds.awaitAll()
            for (i in types.indices) {
                if (prevRes[i] != null) {
                    prevRes[i]!!.imageType = types[i]
                }
            }
            val res: List<ImageResponse> = prevRes.filter { it != null && it.total > 0 } as List<ImageResponse>
            res.ifEmpty { null }
        } catch (t: Throwable) {
            null
        }
    }

    private suspend fun getSimilarFilms(id: Long): List<FilmPreview>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getSimilarFilms(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getSimilarFilms(id, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!.items
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    private suspend fun getSeasons(id: Long): List<Season>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getSeasons(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getSeasons(id, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!.items
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    suspend fun getActorDetail(id: Long): DetailActor? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getActorDetail(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getActorDetail(id, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    suspend fun getDopInfoForFilm(id: Long): DopInfoForFilm? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getDopInfoForFilm(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getDopInfoForFilm(id, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    suspend fun getImagesForFilmPaging(id: Long, page: Int, type: String): ImageResponse? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getImagesInFilm(id, type, page, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getImagesInFilm(id, type, page, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }
}
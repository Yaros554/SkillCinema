package com.skyyaros.skillcinema.data

import androidx.annotation.VisibleForTesting
import com.skyyaros.skillcinema.entity.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class KinopoiskRepositoryDefault(private val kinopoiskApi: KinopoiskApi) : KinopoiskRepository {
    private val cacheFilms = mutableMapOf<Long, FilmPreview>()
    private val mutex = Mutex()
    private val permissionsPreloadPhotography = mapOf(
        Pair("Home", mutableMapOf<String, Boolean>()),
        Pair("Search", mutableMapOf<String, Boolean>()),
        Pair("Profile", mutableMapOf<String, Boolean>()),
    )
    private val permissionsPreloadViewPager = mutableMapOf(
        Pair("Home", true),
        Pair("Search", true),
        Pair("Profile", true)
    )
    fun addPermissionType(curStack: String, imageType: String) {
        permissionsPreloadPhotography[curStack]!![imageType] = true
    }
    fun denyEnablePreload(curStack: String, imageType: String?, isEnabled: Boolean) {
        if (imageType == null)
            permissionsPreloadViewPager[curStack] = isEnabled
        else
            permissionsPreloadPhotography[curStack]!![imageType] = isEnabled
    }
    suspend fun getPermissionPreload(curStack: String, imageType: String?) {
        if (imageType == null) {
            while (!permissionsPreloadViewPager[curStack]!!)
                delay(1)
        }
        else {
            while (!permissionsPreloadPhotography[curStack]!![imageType]!!)
                delay(1)
        }
    }
    var listGenres: List<GenreForFilter>? = null
        private set
    var listCountries: List<CountryForFilter>? = null
        private set
    private var dynamicFilms1: List<FilmPreview>? = null
    private var dynamicFilms2: List<FilmPreview>? = null
    private var countryId1 = -1L; private var countryName1 = ""
    private var genreId1 = -1L; private var genreName1 = ""
    private var countryId2 = -1L; private var countryName2 = ""
    private var genreId2 = -1L; private var genreName2 = ""

    override suspend fun getFilmsForHome(): FilmsPreviewWithData? = coroutineScope {
        dynamicFilms1 = null; dynamicFilms2 = null
        countryId1 = -1L; countryName1 = ""; genreId1 = -1L; genreName1 = ""
        countryId2 = -1L; countryName2 = ""; genreId2 = -1L; genreName2 = ""
        try {
            val genresAndCountriesResponse = getGenresAndCountries()
            if (genresAndCountriesResponse.isSuccessful) {
                listGenres = genresAndCountriesResponse.body()!!.genres.filter { it.genre.isNotEmpty() }
                listCountries = genresAndCountriesResponse.body()!!.countries.filter { it.country.isNotEmpty() }
                getDynamicFilms1()
                if (dynamicFilms1 != null) {
                    getDynamicFilms2()
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
                countryId1, countryName1, genreId1, genreName1,
                countryId2, countryName2, genreId2, genreName2
            )
        } else {
            null
        }
    }

    @VisibleForTesting
    override suspend fun getGenresAndCountries(): Response<GenresAndCountriesResponse> {
        var apiKey = KinopoiskApi.getCurrentKey()
        var genresAndCountriesResponse = kinopoiskApi.getGenresAndCountries(apiKey)
        var count = 1
        val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
        while ((genresAndCountriesResponse.code() == 402 || genresAndCountriesResponse.code() == 429) && count < keysDatabaseSize) {
            apiKey = KinopoiskApi.getNewKey(apiKey)
            genresAndCountriesResponse = kinopoiskApi.getGenresAndCountries(apiKey)
            count++
        }
        return genresAndCountriesResponse
    }

    private suspend fun getDynamicFilms1() {
        var needFilms = true
        while (needFilms) {
            val indexCountry = kotlin.random.Random.nextInt(0, listCountries!!.size)
            val indexGenre = kotlin.random.Random.nextInt(0, listGenres!!.size)
            countryId1 = listCountries!![indexCountry].id
            genreId1 = listGenres!![indexGenre].id
            val listFilms = getFiltersOrSeriesForHome(countryId1, genreId1)
            if (listFilms == null) {
                needFilms = false
            } else if (listFilms.size > 5) {
                countryName1 = listCountries!![indexCountry].country
                genreName1 = listGenres!![indexGenre].genre
                dynamicFilms1 = listFilms
                needFilms = false
            }
        }
    }

    private suspend fun getDynamicFilms2() {
        var needFilms = true
        while (needFilms) {
            var indexCountry: Int
            var indexGenre: Int
            do {
                indexCountry = kotlin.random.Random.nextInt(0, listCountries!!.size)
                indexGenre = kotlin.random.Random.nextInt(0, listGenres!!.size)
                countryId2 = listCountries!![indexCountry].id
                genreId2 = listGenres!![indexGenre].id
            } while (countryId2 == countryId1 && genreId2 == genreId1)
            val listFilms = getFiltersOrSeriesForHome(countryId2, genreId2)
            if (listFilms == null) {
                needFilms = false
            } else if (listFilms.size > 5) {
                countryName2 = listCountries!![indexCountry].country
                genreName2 = listGenres!![indexGenre].genre
                dynamicFilms2 = listFilms
                needFilms = false
            }
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
                val films = res.body()!!.usefulData!!.filter {
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
                        val films2 = res2.body()!!.usefulData!!.filter {
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

    override suspend fun getTopForHome(type: String, page: Int): List<FilmPreview>? {
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
                res.body()!!.usefulDataTop!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    override suspend fun getFiltersOrSeriesForHome(countries: Long?, genres: Long?, page: Int): List<FilmPreview>? {
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
                res.body()!!.usefulData!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    override suspend fun getFullDetailFilm(id: Long): FullDetailFilm? {
        val detailFilm = getDetailFilm(id)
        return if (detailFilm != null) {
            val actors = getActorsInFilm(id)
            val images = getImagesInFilm(id)
            val videos = getVideosInFilm(id)
            val similarsHalf = getSimilarFilms(id)
            val similars10 = if (similarsHalf != null)
                getFilmPreviewPage(similarsHalf, 1)
            else
                null
            val seasons = if (detailFilm.type == "TV_SERIES" || detailFilm.type == "MINI_SERIES" || detailFilm.type == "TV_SHOW")
                getSeasons(id)
            else
                null
            val money = getMoney(id)
            FullDetailFilm(detailFilm, actors, images, videos, similarsHalf, similars10, seasons, money)
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
            val deferreds = types.map { async { getImagesForFilmPaging(id, 1, it) } }
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

    private suspend fun getVideosInFilm(id: Long): List<VideoItem>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getVideoInFilm(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getVideoInFilm(id, apiKey)
                count++
            }
            if (res.isSuccessful && res.body()!!.total > 0) {
                res.body()!!.items
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    private suspend fun getSimilarFilms(id: Long): List<FilmPreviewHalf>? {
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

    override suspend fun getActorDetail(id: Long): DetailActor? {
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
                val item = res.body()!!
                item.listBestFilmPreviewHalf = item.listFilmPreviewHalf?.filter {
                    val rating = it.rating?.toDoubleOrNull() ?: (it.rating?.substring(0, 2)?.toIntOrNull()?.div(10.0) ?: 0.0)
                    rating >= 7.0
                }?.distinctBy { it.filmId }
                if (item.listBestFilmPreviewHalf != null)
                    item.best10Films = getFilmPreviewPage(item.listBestFilmPreviewHalf!!, 1)
                item
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    override suspend fun getFilmPreviewPage(listFilmPreviewHalf: List<FilmPreviewHalf>, page: Int): List<FilmPreview> = coroutineScope {
        var upEdge = page * 10 - 1
        val downEdge = page * 10 - 10
        if (downEdge > listFilmPreviewHalf.lastIndex)
            return@coroutineScope emptyList()
        if (upEdge > listFilmPreviewHalf.lastIndex)
            upEdge = listFilmPreviewHalf.lastIndex
        val deferreds = listFilmPreviewHalf.subList(downEdge, upEdge + 1).map {
            async {
                try {
                    if (cacheFilms.containsKey(it.filmId))
                        cacheFilms[it.filmId]!!
                    else {
                        var apiKey = KinopoiskApi.getCurrentKey()
                        var res = kinopoiskApi.getFilmPreview(it.filmId, apiKey)
                        var count = 1
                        val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
                        while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                            apiKey = KinopoiskApi.getNewKey(apiKey)
                            res = kinopoiskApi.getFilmPreview(it.filmId, apiKey)
                            count++
                        }
                        if (res.isSuccessful) {
                            val resBody = res.body()!!
                            resBody.rating = it.rating
                            mutex.withLock { cacheFilms += Pair(it.filmId, resBody) }
                            resBody
                        } else {
                            FilmPreview(
                                it.filmId, null, it.imageUrl,
                                it.nameRu, it.nameEn, null,
                                null, it.rating, null,
                                null, null
                            )
                        }
                    }
                } catch (t: Throwable) {
                    FilmPreview(
                        it.filmId, null, it.imageUrl,
                        it.nameRu, it.nameEn, null,
                        null, it.rating, null,
                        null, null
                    )
                }
            }
        }
        return@coroutineScope deferreds.awaitAll()
    }

    private data class ImageResponseWithTimestamp(
        val imageResponse: ImageResponse,
        var timeStamp: Long
    )
    private val imageCache = mutableMapOf<String, ImageResponseWithTimestamp>()
    fun getPageFromUrl(id: Long, type: String, curUrl: String): Int {
        val myRegex = "$id (\\d)+ $type".toRegex()
        val keys = imageCache.keys.toList().filter { myRegex.containsMatchIn(it) }
        for (i in keys) {
            if (imageCache[i]!!.imageResponse.items.map { it.imageUrl }.contains(curUrl)) {
                val realPage = i.split(' ')[1].toInt()
                val realIndex = imageCache[i]!!.imageResponse.items.indexOfFirst {
                    it.imageUrl == curUrl
                } + 20 * (realPage - 1)
                return realIndex / 18 + 1
            }
        }
        return 1
    }

    override suspend fun getImagesForFilmPaging(id: Long, page: Int, type: String): ImageResponse? {
        return try {
            val key = "$id $page $type"
            if (imageCache[key] != null) {
                imageCache[key]!!.timeStamp = System.currentTimeMillis()
                imageCache[key]!!.imageResponse
            } else {
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
                    imageCache[key] = ImageResponseWithTimestamp(res.body()!!, System.currentTimeMillis())
                    if (imageCache.size > 100) {
                        val oldestValue = imageCache.values.toList().minBy { it.timeStamp }
                        val oldestKey = imageCache.keys.first { imageCache[it] == oldestValue }
                        imageCache.remove(oldestKey)
                    }
                    res.body()!!
                } else {
                    null
                }
            }
        } catch (t: Throwable) {
            null
        }
    }

    private suspend fun getMoney(id: Long): List<MoneyInfo>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getMoney(id, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getMoney(id, apiKey)
                count++
            }
            if (res.isSuccessful) {
                val data = res.body()!!.items
                data.ifEmpty { null }
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    override suspend fun getSearchFilms(searchQuery: SearchQuery, page: Int): List<FilmPreview>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getSearchFilms(
                searchQuery.countries, searchQuery.genres,
                searchQuery.order, searchQuery.type,
                searchQuery.ratingFrom, searchQuery.ratingTo,
                searchQuery.yearFrom, searchQuery.yearTo,
                searchQuery.keyword, page,
                apiKey
            )
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getSearchFilms(
                    searchQuery.countries, searchQuery.genres,
                    searchQuery.order, searchQuery.type,
                    searchQuery.ratingFrom, searchQuery.ratingTo,
                    searchQuery.yearFrom, searchQuery.yearTo,
                    searchQuery.keyword, page,
                    apiKey
                )
                count++
            }
            if (res.isSuccessful) {
                res.body()!!.usefulData!!
            } else {
                null
            }
        } catch (t: Throwable) {
            null
        }
    }

    override suspend fun getSearchActors(name: String, page: Int): List<SearchActor>? {
        return try {
            var apiKey = KinopoiskApi.getCurrentKey()
            var res = kinopoiskApi.getSearchActors(name, page, apiKey)
            var count = 1
            val keysDatabaseSize = KinopoiskApi.getKeyBaseSize()
            while ((res.code() == 402 || res.code() == 429) && count < keysDatabaseSize) {
                apiKey = KinopoiskApi.getNewKey(apiKey)
                res = kinopoiskApi.getSearchActors(name, page, apiKey)
                count++
            }
            if (res.isSuccessful) {
                res.body()!!.items
            } else {
                null
            }
        } catch(t: Throwable) {
            null
        }
    }
}
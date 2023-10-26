package com.skyyaros.skillcinema

import com.google.gson.Gson
import com.skyyaros.skillcinema.data.KinopoiskApi
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.entity.FilmsPreviewWithData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import java.io.File

//это не тесты! это вспомогательный класс для генерации реалистичных данных для тестов
class TestDataGen {
    private val api = KinopoiskApi.provide()
    private val kinopoiskRepositoryDefault = KinopoiskRepositoryDefault(api)

    @Test
    fun createTestDataHomeFragment() = runBlocking {
        val homeFragmentData = kinopoiskRepositoryDefault.getFilmsForHome()
        assertThat(homeFragmentData != null, `is`(true))
        assertThat(homeFragmentData!!.films != null && homeFragmentData.films.all { it != null }, `is`(true))
        val myGson = Gson().toJson(homeFragmentData)
        File("testData/TestDataHomeFragment.txt").writeText(myGson)
    }

    @Test
    fun createTestDataDetailFilm() = runBlocking {
        val myGson = File("testData/TestDataHomeFragment.txt").readText()
        val filmsPreviewWithData = Gson().fromJson(myGson, FilmsPreviewWithData::class.java)
        val filmId = filmsPreviewWithData.films[0]!![0].kinopoiskId
        assertThat(filmId, `is`(4959134L))
        val detailFilm = kinopoiskRepositoryDefault.getFullDetailFilm(filmId!!)
        val myGson2 = Gson().toJson(detailFilm)
        File("testData/TestDataDetailFilmFragment.txt").writeText(myGson2)
    }
}
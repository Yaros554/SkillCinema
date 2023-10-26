package com.skyyaros.skillcinema

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.skyyaros.skillcinema.data.KinopoiskApi
import com.skyyaros.skillcinema.data.KinopoiskRepositoryDefault
import com.skyyaros.skillcinema.entity.FilmsPreviewWithData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import java.io.File

//это не тесты! это вспомогательный класс для генерации реалистичных данных для тестов
class TestDataGen {
    private val api = KinopoiskApi.provide()
    private val kinopoiskRepositoryDefault = KinopoiskRepositoryDefault(api)

    @Test
    fun createTestDataHomeFragment() = runBlocking {
        val homeFragmentData = kinopoiskRepositoryDefault.getFilmsForHome()
        MatcherAssert.assertThat(homeFragmentData != null, CoreMatchers.`is`(true))
        MatcherAssert.assertThat(homeFragmentData!!.films != null && homeFragmentData.films.all { it != null }, CoreMatchers.`is`(true))
        val myGson = Gson().toJson(homeFragmentData)
        val output = InstrumentationRegistry.getInstrumentation().targetContext.openFileOutput(
            "TestDataHomeFragment.txt", Context.MODE_PRIVATE
        )
        withContext(Dispatchers.IO) {
            output.write(myGson.toByteArray())
            output.close()
        }
    }

    @Test
    fun createTestDataDetailFilm() = runBlocking {
        val input = InstrumentationRegistry.getInstrumentation().targetContext.openFileInput(
            "TestDataHomeFragment.txt"
        )
        val myGson = withContext(Dispatchers.IO) {
            val bytes = ByteArray(input.available())
            input.read(bytes)
            input.close()
            String(bytes)
        }
        val filmsPreviewWithData = Gson().fromJson(myGson, FilmsPreviewWithData::class.java)
        val filmId = filmsPreviewWithData.films[0]!![0].kinopoiskId
        val detailFilm = kinopoiskRepositoryDefault.getFullDetailFilm(filmId!!)
        val myGson2 = Gson().toJson(detailFilm)
        val output = InstrumentationRegistry.getInstrumentation().targetContext.openFileOutput(
            "TestDataDetailFilmFragment.txt", Context.MODE_PRIVATE
        )
        withContext(Dispatchers.IO) {
            output.write(myGson2.toByteArray())
            output.close()
        }
    }
}
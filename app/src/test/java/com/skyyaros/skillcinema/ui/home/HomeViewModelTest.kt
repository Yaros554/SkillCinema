package com.skyyaros.skillcinema.ui.home

import com.google.gson.Gson
import com.skyyaros.skillcinema.data.FakeKinopoiskRepository
import com.skyyaros.skillcinema.data.FakeStoreRepository
import com.skyyaros.skillcinema.entity.FilmsPreviewWithData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var fakeKinopoiskRepository: FakeKinopoiskRepository
    private lateinit var fakeStoreRepository: FakeStoreRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispather = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispather)
        fakeKinopoiskRepository = FakeKinopoiskRepository()
        fakeStoreRepository = FakeStoreRepository()
        viewModel = HomeViewModel(fakeStoreRepository, fakeKinopoiskRepository)
    }

    @After
    fun endTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun initAndUpdateFilm() = runTest {
        var resFilms = viewModel.filmsFlow.value
        assertThat(resFilms is StateHomeFilms.Success, `is`(true))
        val myGson = File("testData/TestDataHomeFragment.txt").readText()
        val etalon = Gson().fromJson(myGson, FilmsPreviewWithData::class.java)
        assertThat((resFilms as StateHomeFilms.Success).data, `is`(etalon))

        fakeKinopoiskRepository.needUpdate = true
        viewModel.updateFilms()
        resFilms = viewModel.filmsFlow.value
        assertThat(resFilms, `is`(StateHomeFilms.Loading))
        testDispather.scheduler.runCurrent()
        resFilms = viewModel.filmsFlow.value
        assertThat(resFilms is StateHomeFilms.Success, `is`(true))
        val myGson2 = File("testData/TestDataHomeFragment2.txt").readText()
        val etalon2 = Gson().fromJson(myGson2, FilmsPreviewWithData::class.java)
        assertThat((resFilms as StateHomeFilms.Success).data, `is`(etalon2))
    }

    @Test
    fun initAndUpdateFilmError() = runTest {
        var resFilms = viewModel.filmsFlow.value
        assertThat(resFilms is StateHomeFilms.Success, `is`(true))
        val myGson = File("testData/TestDataHomeFragment.txt").readText()
        val etalon = Gson().fromJson(myGson, FilmsPreviewWithData::class.java)
        assertThat((resFilms as StateHomeFilms.Success).data, `is`(etalon))

        fakeKinopoiskRepository.isError = true
        viewModel.updateFilms()
        resFilms = viewModel.filmsFlow.value
        assertThat(resFilms, `is`(StateHomeFilms.Loading))
        testDispather.scheduler.runCurrent()
        resFilms = viewModel.filmsFlow.value
        assertThat(resFilms is StateHomeFilms.Error, `is`(true))
    }
}
package com.skyyaros.skillcinema.data

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

class KinopoiskRepositoryDefaultTest {
    private lateinit var kinopoiskApi: FakeKinopoiskApi
    private lateinit var kinopoiskRepositoryDefault: KinopoiskRepositoryDefault

    @Before
    fun setup() {
        kinopoiskApi = FakeKinopoiskApi()
        kinopoiskRepositoryDefault = KinopoiskRepositoryDefault(kinopoiskApi)
    }

    @Test
    fun getGenresAndCountriesSuccess() = runTest {
        kinopoiskApi.setError(200)
        val res = kinopoiskRepositoryDefault.getGenresAndCountries()
        assertThat(res.isSuccessful, `is`(true))
        assertThat(res.body()!!, `is`(FakeKinopoiskApi.etalon))
    }

    @Test
    fun getGenresAndCountriesError402() = runTest {
        kinopoiskApi.setError(402, 4)
        val res = kinopoiskRepositoryDefault.getGenresAndCountries()
        assertThat(res.code(), `is`(402))
    }

    @Test
    fun getGenresAndCountriesError402HasKey() = runTest {
        kinopoiskApi.setError(402, 3)
        val res = kinopoiskRepositoryDefault.getGenresAndCountries()
        assertThat(res.isSuccessful, `is`(true))
        assertThat(res.body()!!, `is`(FakeKinopoiskApi.etalon))
    }

    @Test
    fun getGenresAndCountriesError404() = runTest {
        kinopoiskApi.setError(404)
        val res = kinopoiskRepositoryDefault.getGenresAndCountries()
        assertThat(res.code(), `is`(404))
    }
}
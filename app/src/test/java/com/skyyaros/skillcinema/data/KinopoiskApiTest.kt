package com.skyyaros.skillcinema.data

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

class KinopoiskApiTest {
    @Test
    fun testSwitchKey() = runTest {
        var currentKey = KinopoiskApi.getCurrentKey()
        assertThat(currentKey["X-API-KEY"], `is`(KinopoiskApi.keys[0]))
        for (i in 1 until KinopoiskApi.getKeyBaseSize()) {
            currentKey = KinopoiskApi.getNewKey(currentKey)
            assertThat(currentKey["X-API-KEY"], `is`(KinopoiskApi.keys[i]))
        }
        currentKey = KinopoiskApi.getNewKey(currentKey)
        assertThat(currentKey["X-API-KEY"], `is`(KinopoiskApi.keys[0]))
    }
}
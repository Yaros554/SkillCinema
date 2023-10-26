package com.skyyaros.skillcinema.ui.home

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.skyyaros.skillcinema.ServiceLocator
import com.skyyaros.skillcinema.data.FakeKinopoiskRepository
import com.skyyaros.skillcinema.data.FakeStoreRepository
import com.skyyaros.skillcinema.data.KinopoiskRepository
import com.skyyaros.skillcinema.data.StoreRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.skyyaros.skillcinema.R
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {
    private lateinit var kinopoiskRepository: KinopoiskRepository
    private lateinit var storeRepository: StoreRepository

    @Before
    fun setup() {
        kinopoiskRepository = FakeKinopoiskRepository()
        ServiceLocator.fakeKinopoiskRepository = kinopoiskRepository
        storeRepository = FakeStoreRepository()
        ServiceLocator.fakeStoreRepository = storeRepository
    }

    @After
    fun cleanUp() {
        ServiceLocator.fakeKinopoiskRepository = null
        ServiceLocator.fakeStoreRepository = null
    }

    @Test
    fun textAllIsSee() = runTest {
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.Theme_SkillCinema)
        onView(withId(R.id.text_premiers_all)).check(matches(isDisplayed()))
    }
}
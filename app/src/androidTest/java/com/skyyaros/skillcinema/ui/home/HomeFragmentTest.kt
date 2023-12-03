package com.skyyaros.skillcinema.ui.home

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
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
import com.skyyaros.skillcinema.data.FakeActivityCallbacks
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.MyCountingIdlingResource
import org.hamcrest.core.IsNot.not
import org.mockito.Mockito.verify
import org.mockito.Mockito.mock
import java.util.Locale

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {
    private lateinit var kinopoiskRepository: FakeKinopoiskRepository
    private lateinit var storeRepository: StoreRepository
    private lateinit var activityCallbacks: ActivityCallbacks

    @Before
    fun setup() {
        kinopoiskRepository = FakeKinopoiskRepository()
        ServiceLocator.fakeKinopoiskRepository = kinopoiskRepository
        storeRepository = FakeStoreRepository()
        ServiceLocator.fakeStoreRepository = storeRepository
        activityCallbacks = FakeActivityCallbacks()
        ServiceLocator.fakeActivityCallbacks = activityCallbacks
        IdlingRegistry.getInstance().register(MyCountingIdlingResource.countingIdlingResource)
    }

    @After
    fun cleanUp() {
        IdlingRegistry.getInstance().unregister(MyCountingIdlingResource.countingIdlingResource)
        ServiceLocator.fakeKinopoiskRepository = null
        ServiceLocator.fakeStoreRepository = null
        ServiceLocator.fakeActivityCallbacks = null
    }

    @Test
    fun textAllIsSee() {
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.Theme_SkillCinema)
        onView(withId(R.id.text_premiers)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.text_populars)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.text_dynamic1)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.text_top)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.text_dynamic2)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.text_serials)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.button_reload)).check(matches(not(isDisplayed())))
    }

    @Test
    fun textAllError() {
        kinopoiskRepository.isError = true
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.Theme_SkillCinema)
        onView(withId(R.id.text_premiers)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_populars)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_dynamic1)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_top)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_dynamic2)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_serials)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.button_reload)).check(matches(isDisplayed()))
    }

    @Test
    fun goToFilm() {
        val scenario = launchFragmentInContainer<HomeFragment>(Bundle(), R.style.Theme_SkillCinema)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.recycler_premiers)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                ViewMatchers.hasDescendant(
                    ViewMatchers.withText(if (Locale.getDefault().language == "ru") "Тигр 3" else "Tiger 3")
                ),
                ViewActions.click()
            )
        )
        verify(navController).navigate(HomeFragmentDirections.actionHomeFragmentToDetailFilmFragment(4894012L))
    }
}
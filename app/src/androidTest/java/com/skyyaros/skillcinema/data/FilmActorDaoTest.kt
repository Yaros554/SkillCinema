package com.skyyaros.skillcinema.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.Genre
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class FilmActorDaoTest {
    private val filmActorList = listOf(
        FilmActorTable(
            DefaultCats.HistorySee.code, 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5"
        ),
        FilmActorTable(
            DefaultCats.HistorySearch.code, 2L, false, "test url 2",
            "Имя 2", "Name 2", "Name Original 2",
            listOf(Genre("История"), Genre("Военный")), "9.4"
        ),
        FilmActorTable(
            DefaultCats.WantSee.code, 3L, false, "test url 3",
            "Имя 3", "Name 3", "Name Original 3",
            listOf(Genre("Ужасы")), "9.3"
        ),
        FilmActorTable(
            "Мое кинцо", 4L, false, "test url 4",
            "Имя 4", "Name 4", "Name Original 4",
            listOf(Genre("Ужасы"), Genre("Драма")), "1.3"
        )
    )
    private lateinit var database: AppDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        runBlocking {
            database.filmActorDao().insertAllFilmCat(filmActorList.subList(0, 3))
            database.filmActorDao().insertFilmOrCat(filmActorList[3])
        }
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertAndGetHistorySee() {
        var historySee: List<FilmActorTable>? = null
        var job: Job? = null
        var timer: Job? = null
        var count = 0
        runBlocking {
            database.filmActorDao().insertAllFilmCat(filmActorList.subList(0, 3))
            database.filmActorDao().insertFilmOrCat(filmActorList[3])
            job = launch {
                database.filmActorDao().getHistory(DefaultCats.HistorySee.code)
                    .stateIn(this, SharingStarted.WhileSubscribed(1000), emptyList())
                    .collect {
                        if (it.isEmpty() && count == 0)
                            count++
                        else {
                            historySee = it
                            timer?.cancel()
                            job!!.cancel()
                        }
                    }
            }
            timer = launch { startTimer(job) }
        }
        assertThat(historySee != null, `is`(true))
        assertThat(historySee!!.size, `is`(1))
        assertThat(historySee!![0], `is`(filmActorList[0]))
    }

    @Test
    fun insertAndGetHistorySee2() = runTest {
        //database.filmActorDao().insertAllFilmCat(filmActorList.subList(0, 3))
        //database.filmActorDao().insertFilmOrCat(filmActorList[3])
        val historySeeFlow = database.filmActorDao().getHistory(DefaultCats.HistorySee.code)
            .stateIn(this, SharingStarted.Eagerly, emptyList())
        testScheduler.runCurrent()
        val historySee = historySeeFlow.value
        assertThat(historySee.size, `is`(1))
        assertThat(historySee[0], `is`(filmActorList[0]))
    }

    @Test
    fun insertAndGetCat() {
        var filmsInCats: List<FilmActorTable>? = null
        var job: Job? = null
        var timer: Job? = null
        var count = 0
        runBlocking {
            database.filmActorDao().insertAllFilmCat(filmActorList.subList(0, 3))
            database.filmActorDao().insertFilmOrCat(filmActorList[3])
            job = launch {
                database.filmActorDao().getFilmActorWithCat()
                    .stateIn(this, SharingStarted.WhileSubscribed(1000), emptyList())
                    .collect {
                        if (it.isEmpty() && count == 0)
                            count++
                        else {
                            filmsInCats = it
                            timer?.cancel()
                            job!!.cancel()
                        }
                    }
            }
            timer = launch { startTimer(job) }
        }
        assertThat(filmsInCats != null, `is`(true))
        assertThat(filmsInCats!!.size, `is`(2))
        assertThat(filmsInCats!![0], `is`(filmActorList[2]))
        assertThat(filmsInCats!![1], `is`(filmActorList[3]))
    }

    @Test
    fun deleteFilm() {
        var filmsInCats: List<FilmActorTable>? = null
        var job: Job? = null
        var timer: Job? = null
        var count = 0
        runBlocking {
            database.filmActorDao().insertAllFilmCat(filmActorList)
            database.filmActorDao().deleteFilm(3L, DefaultCats.WantSee.code)
            job = launch {
                database.filmActorDao().getFilmActorWithCat()
                    .stateIn(this, SharingStarted.WhileSubscribed(1000), emptyList())
                    .collect {
                        if (it.isEmpty() && count == 0)
                            count++
                        else {
                            filmsInCats = it
                            timer?.cancel()
                            job!!.cancel()
                        }
                    }
            }
            timer = launch { startTimer(job) }
        }
        assertThat(filmsInCats != null, `is`(true))
        assertThat(filmsInCats!!.size, `is`(1))
        assertThat(filmsInCats!![0], `is`(filmActorList[3]))
    }

    private suspend fun startTimer(job: Job?) {
        var time = 5000
        while (time > 0) {
            time--
            delay(1)
        }
        job?.cancel()
    }
}
package com.skyyaros.skillcinema.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.Genre
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

@RunWith(AndroidJUnit4::class)
@MediumTest
class DatabaseRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var databaseRepository: DatabaseRepository
    private val filmActorList = listOf(
        FilmActorTable(
            "Cat 1", 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5"
        ),
        FilmActorTable(
            "Cat 2", 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5"
        ),
        FilmActorTable(
            "Cat 3", 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5"
        ),
        FilmActorTable(
            "Cat 4", 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5"
        )
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        databaseRepository = DatabaseRepository(database)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun updateFilmCat_newCat() {
        var filmsInCats: List<FilmActorTable>? = null
        var job: Job? = null
        var timer: Job? = null
        var count = 0
        runBlocking {
            database.filmActorDao().insertAllFilmCat(filmActorList.subList(0, 2))
            databaseRepository.updateFilmCat(1L, filmActorList.subList(2, 4))
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
    fun updateFilmCat_noCat() {
        var filmsInCats: List<FilmActorTable>? = null
        var job: Job? = null
        var timer: Job? = null
        var count = 0
        runBlocking {
            database.filmActorDao().insertAllFilmCat(filmActorList)
            databaseRepository.updateFilmCat(1L, emptyList())
            job = launch {
                database.filmActorDao().getFilmActorWithCat()
                    .stateIn(this, SharingStarted.WhileSubscribed(1000), filmActorList)
                    .collect {
                        if (it == filmActorList && count == 0)
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
        assertThat(filmsInCats!!.isEmpty(), `is`(true))
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
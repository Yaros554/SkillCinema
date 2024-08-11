package com.skyyaros.skillcinema.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.Genre
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 1L
        ),
        FilmActorTable(
            "Cat 2", 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 2L
        ),
        FilmActorTable(
            "Cat 3", 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 3L
        ),
        FilmActorTable(
            "Cat 4", 1L, false, "test url",
            "Имя", "Name", "Name Original",
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 4L
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
    fun updateFilmCat_newCat() = runTest {
        database.filmActorDao().insertAllFilmCat(filmActorList.subList(0, 2))
        databaseRepository.updateFilmCat(1L, filmActorList.subList(2, 4))
        val filmsInCats = database.filmActorDao().getFilmActorWithCat().first().sortedBy { it.dateCreate }
        assertThat(filmsInCats.size, `is`(2))
        assertThat(filmsInCats, `is`(filmActorList.subList(2, 4).sortedBy { it.dateCreate }))
    }

    @Test
    fun updateFilmCat_noCat() = runTest {
        database.filmActorDao().insertAllFilmCat(filmActorList)
        databaseRepository.updateFilmCat(1L, emptyList())
        val filmsInCats = database.filmActorDao().getFilmActorWithCat().first()
        assertThat(filmsInCats.isEmpty(), `is`(true))
    }
}
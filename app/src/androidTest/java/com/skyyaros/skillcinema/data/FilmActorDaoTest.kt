package com.skyyaros.skillcinema.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.Genre
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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
            listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 1L
        ),
        FilmActorTable(
            DefaultCats.HistorySearch.code, 2L, false, "test url 2",
            "Имя 2", "Name 2", "Name Original 2",
            listOf(Genre("История"), Genre("Военный")), "9.4", 2L
        ),
        FilmActorTable(
            DefaultCats.WantSee.code, 3L, false, "test url 3",
            "Имя 3", "Name 3", "Name Original 3",
            listOf(Genre("Ужасы")), "9.3", 3L
        ),
        FilmActorTable(
            "Мое кинцо", 4L, false, "test url 4",
            "Имя 4", "Name 4", "Name Original 4",
            listOf(Genre("Ужасы"), Genre("Драма")), "1.3", 4L
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
    fun insertAndGetHistorySee() = runTest {
        val historySee = database.filmActorDao().getHistory(DefaultCats.HistorySee.code).first()
        assertThat(historySee.size, `is`(1))
        assertThat(historySee[0], `is`(filmActorList[0]))
    }

    @Test
    fun insertAndGetCat() = runTest {
        val filmsInCats = database.filmActorDao().getFilmActorWithCat().first().sortedBy { it.dateCreate }
        assertThat(filmsInCats.size, `is`(2))
        assertThat(filmsInCats, `is`(filmActorList.subList(2, 4).sortedBy { it.dateCreate }))
    }

    @Test
    fun deleteFilm() = runTest {
        database.filmActorDao().deleteFilm(3L, DefaultCats.WantSee.code)
        val filmsInCats = database.filmActorDao().getFilmActorWithCat().first()
        assertThat(filmsInCats.size, `is`(1))
        assertThat(filmsInCats[0], `is`(filmActorList[3]))
    }

    @Test
    fun deleteFilm2() = runTest {
        val filmForDelete = FilmActorTable(
            DefaultCats.WantSee.code, 3L, false, "test url 3",
            "Имя 3", "Name 3", "Name Original 3",
            listOf(Genre("Ужасы")), "9.3", 5L
        )
        database.filmActorDao().deleteFilmActor(filmForDelete)
        val filmsInCats = database.filmActorDao().getFilmActorWithCat().first()
        assertThat(filmsInCats.size, `is`(1))
        assertThat(filmsInCats[0], `is`(filmActorList[3]))
    }

    @Test
    fun deleteCategory() = runTest {
        val myCat = FilmActorTable(
            "Мое кинцо", -1L, null, null,
            null, null, null,
            null, null, 5L
        )
        database.filmActorDao().insertFilmOrCat(myCat)
        database.filmActorDao().deleteCategory("Мое кинцо")
        val filmsInCats = database.filmActorDao().getFilmActorWithCat().first()
        assertThat(filmsInCats.size, `is`(1))
        assertThat(filmsInCats[0], `is`(filmActorList[2]))
    }

    @Test
    fun deleteAllFilmCat() = runTest {
        val newData = listOf(
            FilmActorTable(
                DefaultCats.HistorySearch.code, 1L, false, "test url",
                "Имя", "Name", "Name Original",
                listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 6L
            ),
            FilmActorTable(
                DefaultCats.WantSee.code, 1L, false, "test url",
                "Имя", "Name", "Name Original",
                listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 7L
            ),
            FilmActorTable(
                "Мое кинцо", 1L, false, "test url",
                "Имя", "Name", "Name Original",
                listOf(Genre("Боевик"), Genre("Комедия")), "9.5", 8L
            )
        )
        database.filmActorDao().insertAllFilmCat(newData)
        database.filmActorDao().deleteAllFilmCat(1L)
        val historySee = database.filmActorDao().getHistory(DefaultCats.HistorySee.code).first()
        assertThat(historySee.size, `is`(1))
        assertThat(historySee[0], `is`(filmActorList[0]))

        val historySearch = database.filmActorDao().getHistory(DefaultCats.HistorySearch.code).first().sortedBy { it.dateCreate }
        assertThat(historySearch.size, `is`(2))
        assertThat(historySearch[0], `is`(filmActorList[1]))
        assertThat(historySearch[1], `is`(newData[0]))

        val categories = database.filmActorDao().getFilmActorWithCat().first().sortedBy { it.dateCreate }
        assertThat(categories.size, `is`(2))
        assertThat(categories, `is`(filmActorList.subList(2, 4).sortedBy { it.dateCreate }))
    }
}
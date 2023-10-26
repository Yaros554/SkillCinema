package com.skyyaros.skillcinema.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skyyaros.skillcinema.entity.FilmActorTable
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmActorDao {
    @Query("SELECT * FROM filmActorTable WHERE category != :catHistorySee AND category != :catHistorySearch")
    fun getFilmActorWithCat(
        catHistorySee: String = DefaultCats.HistorySee.code,
        catHistorySearch: String = DefaultCats.HistorySearch.code
    ): Flow<List<FilmActorTable>>

    @Query("SELECT * FROM filmActorTable WHERE category == :catHistory")
    fun getHistory(catHistory: String): Flow<List<FilmActorTable>>

    @Query("DELETE FROM filmActorTable WHERE kinopoiskId == :id AND category != :catHistorySee AND category != :catHistorySearch")
    suspend fun deleteAllFilmCat(
        id: Long,
        catHistorySee: String = DefaultCats.HistorySee.code,
        catHistorySearch: String = DefaultCats.HistorySearch.code
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilmCat(listFilmCat: List<FilmActorTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilmOrCat(filmOrCat: FilmActorTable)

    @Query("DELETE FROM filmActorTable WHERE kinopoiskId == :filmId AND category == :category")
    suspend fun deleteFilm(filmId: Long, category: String)

    @Query("DELETE FROM filmActorTable WHERE category == :category")
    suspend fun deleteCategory(category: String)

    @Delete
    suspend fun deleteFilmActor(filmActorTable: FilmActorTable)
}
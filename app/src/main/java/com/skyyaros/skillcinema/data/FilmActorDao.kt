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
    @Query("SELECT * FROM filmActorTable WHERE category != :catSee AND category != :catSearch")
    fun getFilmActorWithCat(catSee: String = "0", catSearch: String = "1"): Flow<List<FilmActorTable>>

    @Query("SELECT * FROM filmActorTable WHERE category == :catHistory")
    fun getHistory(catHistory: String): Flow<List<FilmActorTable>>

    @Query("DELETE FROM filmActorTable WHERE kinopoiskId == :id AND category != :catSee AND category != :catSearch")
    suspend fun deleteAllFilmCat(id: Long, catSee: String = "0", catSearch: String = "1")

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
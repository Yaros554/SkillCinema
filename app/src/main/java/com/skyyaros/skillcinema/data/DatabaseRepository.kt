package com.skyyaros.skillcinema.data

import com.skyyaros.skillcinema.entity.FilmActorTable
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(private val appDatabase: AppDatabase) {
    fun getFilmActorWithCat(): Flow<List<FilmActorTable>> {
        return appDatabase.filmActorDao().getFilmActorWithCat()
    }

    fun getSearchHistory(catHistory: String): Flow<List<FilmActorTable>> {
        return appDatabase.filmActorDao().getHistory(catHistory)
    }

    suspend fun updateFilmCat(id:Long, newCategory: List<FilmActorTable>) {
        appDatabase.filmActorDao().deleteAllFilmCat(id)
        if (newCategory.isNotEmpty())
            appDatabase.filmActorDao().insertAllFilmCat(newCategory)
    }

    suspend fun insertFilmOrCat(filmOrCat: FilmActorTable) {
        appDatabase.filmActorDao().insertFilmOrCat(filmOrCat)
    }

    suspend fun deleteFilm(filmId: Long, category: String) {
        appDatabase.filmActorDao().deleteFilm(filmId, category)
    }

    suspend fun deleteCategory(category: String) {
        appDatabase.filmActorDao().deleteCategory(category)
    }

    suspend fun deleteFilmActor(filmActorTable: FilmActorTable) {
        appDatabase.filmActorDao().deleteFilmActor(filmActorTable)
    }
}
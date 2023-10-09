package com.skyyaros.skillcinema.ui

import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ActivityCallbacks {
    fun showDownBar()
    fun hideDownBar()
    fun showUpBar(label: String)
    fun hideUpBar()
    fun fullScreenOn()
    fun fullScreenOff()
    fun goToFullScreenMode(needGo: Boolean)
    fun emitResultFV(mode: Int, isChecked: Boolean)
    fun getResultStreamFV(mode: Int): SharedFlow<Boolean>
    fun getSearchQuery(): SearchQuery
    fun setSearchQuery(searchQuery: SearchQuery)
    fun emitResBackDialog(userSelect: Int)
    fun getResStreamBackDialog(): SharedFlow<Int>
    fun emitGenreCountry(id: Long)
    fun getGenreCountryFlow(): SharedFlow<Long>
    fun cleanGenreCountry()
    fun emitYear(years: Int)
    fun getYearFlow(): SharedFlow<Int>
    fun cleanYear()
    fun emitNewCat(newCat: String)
    fun getNewCatFlow(): SharedFlow<String>
    fun cleanNewCat()
    fun emitBottomSh()
    fun getBottomShFlow(): SharedFlow<Boolean>
    fun cleanBottomSh()
    fun emitDeleteCat(category: String)
    fun getDeleteCatFlow(): SharedFlow<String>
    fun cleanDeleteCat()
    fun getActorFilmWithCatFlow(): StateFlow<List<FilmActorTable>>
    fun getSearchHistoryFlow(): StateFlow<List<FilmActorTable>>
    fun getSeeHistoryFlow(): StateFlow<List<FilmActorTable>>
    fun getTempFilmActorList(): List<FilmActorTable>?
    fun setTempFilmActorList(newList: List<FilmActorTable>?)
    fun updateFilmCat(id:Long, newCategory: List<FilmActorTable>)
    fun insertFilmOrCat(filmOrCat: FilmActorTable)
    fun insertHistoryItem(filmActorTable: FilmActorTable)
    fun deleteFilm(filmId: Long, category: String)
    fun deleteCategory(category: String)
}
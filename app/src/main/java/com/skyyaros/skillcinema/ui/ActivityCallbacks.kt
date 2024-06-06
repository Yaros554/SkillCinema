package com.skyyaros.skillcinema.ui

import com.skyyaros.skillcinema.entity.AppSettings
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
    fun getSearchQuery(): SearchQuery
    fun setSearchQuery(searchQuery: SearchQuery)
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
    fun getStartStatusFlow(): StateFlow<Boolean>
    fun setStartStatus(startStatus: Boolean)
    fun getDialogStatusFlow(mode: FullscreenDialogInfoMode): StateFlow<Boolean>
    fun setDialogStatus(mode: FullscreenDialogInfoMode, isShow: Boolean)
    fun getAppSettingsFlow(): StateFlow<AppSettings?>
    fun saveSettings(newSettings: AppSettings)
    fun getUrlPosAnim(curStack: String): String
    fun setUrlPosAnim(curStack: String, urlPos: String)
}
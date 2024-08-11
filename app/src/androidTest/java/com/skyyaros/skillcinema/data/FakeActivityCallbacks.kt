package com.skyyaros.skillcinema.data

import com.skyyaros.skillcinema.entity.AppSettings
import com.skyyaros.skillcinema.entity.AppTheme
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.entity.VideoSource
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.FullscreenDialogInfoMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeActivityCallbacks: ActivityCallbacks {
    private val fakeStoreFlow = MutableStateFlow(false)
    private val fakeAppSettingsFlow = MutableStateFlow(AppSettings(VideoSource.ANY, AppTheme.AUTO, false))

    override fun showDownBar() {}

    override fun hideDownBar() {}

    override fun showUpBar(label: String) {}

    override fun hideUpBar() {}

    override fun fullScreenOn() {}

    override fun fullScreenOff() {}

    override fun goToFullScreenMode(needGo: Boolean) {}

    override fun getSearchQuery(): SearchQuery {
        TODO("Not yet implemented")
    }

    override fun setSearchQuery(searchQuery: SearchQuery) {
        TODO("Not yet implemented")
    }

    override fun getActorFilmWithCatFlow(): StateFlow<List<FilmActorTable>> {
        TODO("Not yet implemented")
    }

    override fun getSearchHistoryFlow(): StateFlow<List<FilmActorTable>> {
        TODO("Not yet implemented")
    }

    override fun getSeeHistoryFlow(): StateFlow<List<FilmActorTable>> {
        TODO("Not yet implemented")
    }

    override fun getTempFilmActorList(): List<FilmActorTable>? {
        TODO("Not yet implemented")
    }

    override fun setTempFilmActorList(newList: List<FilmActorTable>?) {
        TODO("Not yet implemented")
    }

    override fun updateFilmCat(id: Long, newCategory: List<FilmActorTable>) {
        TODO("Not yet implemented")
    }

    override fun insertFilmOrCat(filmOrCat: FilmActorTable) {
        TODO("Not yet implemented")
    }

    override fun insertHistoryItem(filmActorTable: FilmActorTable) {
        TODO("Not yet implemented")
    }

    override fun deleteFilm(filmId: Long, category: String) {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(category: String) {
        TODO("Not yet implemented")
    }

    override fun getStartStatusFlow(): StateFlow<Boolean> {
        return fakeStoreFlow.asStateFlow()
    }

    override fun setStartStatus(startStatus: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getDialogStatusFlow(mode: FullscreenDialogInfoMode): StateFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setDialogStatus(mode: FullscreenDialogInfoMode, isShow: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getAppSettingsFlow(): StateFlow<AppSettings?> {
        return fakeAppSettingsFlow.asStateFlow()
    }

    override fun saveSettings(newSettings: AppSettings) {
        TODO("Not yet implemented")
    }

    override fun getUrlPosAnim(curStack: String): String {
        TODO("Not yet implemented")
    }

    override fun setUrlPosAnim(curStack: String, urlPos: String) {
        TODO("Not yet implemented")
    }
}
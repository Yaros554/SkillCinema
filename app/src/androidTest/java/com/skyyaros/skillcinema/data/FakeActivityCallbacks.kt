package com.skyyaros.skillcinema.data

import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class FakeActivityCallbacks: ActivityCallbacks {
    override fun showDownBar() {}

    override fun hideDownBar() {}

    override fun showUpBar(label: String) {}

    override fun hideUpBar() {}

    override fun fullScreenOn() {}

    override fun fullScreenOff() {}

    override fun goToFullScreenMode(needGo: Boolean) {}

    override fun emitResultFV(mode: Int, isChecked: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getResultStreamFV(mode: Int): SharedFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getSearchQuery(): SearchQuery {
        TODO("Not yet implemented")
    }

    override fun setSearchQuery(searchQuery: SearchQuery) {
        TODO("Not yet implemented")
    }

    override fun emitResBackDialog(userSelect: Int) {
        TODO("Not yet implemented")
    }

    override fun getResStreamBackDialog(): SharedFlow<Int> {
        TODO("Not yet implemented")
    }

    override fun emitGenreCountry(id: Long) {
        TODO("Not yet implemented")
    }

    override fun getGenreCountryFlow(): SharedFlow<Long> {
        TODO("Not yet implemented")
    }

    override fun cleanGenreCountry() {
        TODO("Not yet implemented")
    }

    override fun emitYear(years: Int) {
        TODO("Not yet implemented")
    }

    override fun getYearFlow(): SharedFlow<Int> {
        TODO("Not yet implemented")
    }

    override fun cleanYear() {
        TODO("Not yet implemented")
    }

    override fun emitNewCat(newCat: String) {
        TODO("Not yet implemented")
    }

    override fun getNewCatFlow(): SharedFlow<String> {
        TODO("Not yet implemented")
    }

    override fun cleanNewCat() {
        TODO("Not yet implemented")
    }

    override fun emitBottomSh() {
        TODO("Not yet implemented")
    }

    override fun getBottomShFlow(): SharedFlow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun cleanBottomSh() {
        TODO("Not yet implemented")
    }

    override fun emitDeleteCat(category: String) {
        TODO("Not yet implemented")
    }

    override fun getDeleteCatFlow(): SharedFlow<String> {
        TODO("Not yet implemented")
    }

    override fun cleanDeleteCat() {
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
}
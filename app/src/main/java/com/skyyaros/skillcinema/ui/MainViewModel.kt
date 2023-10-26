package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.DatabaseRepository
import com.skyyaros.skillcinema.data.DefaultCats
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val databaseRepository: DatabaseRepository): ViewModel() {
    var isFullPhotoFragment = false
    private val _resultsF = MutableSharedFlow<Boolean>()
    val resultF = _resultsF.asSharedFlow()
    private val _resultsV = MutableSharedFlow<Boolean>()
    val resultV = _resultsV.asSharedFlow()
    private val _resBackDialog = MutableSharedFlow<Int>()
    val resBackDialog = _resBackDialog.asSharedFlow()
    private val _resGenreCountry = MutableSharedFlow<Long>(1)
    val resGenreCountry = _resGenreCountry.asSharedFlow()
    private val _resYear = MutableSharedFlow<Int>(1)
    val resYear = _resYear.asSharedFlow()
    private val _resAddNewCat = MutableSharedFlow<String>(1)
    val resAddNewCat = _resAddNewCat.asSharedFlow()
    private val _resBottomSh = MutableSharedFlow<Boolean>(1)
    val resBottomSh = _resBottomSh.asSharedFlow()
    private val _resDeleteCat = MutableSharedFlow<String>(1)
    val resDeleteCat = _resDeleteCat.asSharedFlow()
    var searchQuery = SearchQuery(
        null, null, "NUM_VOTE", "ALL",
        0, 10, 1000, 3000,
        null, null, true
    )
    val filmActorWithCatFlow = databaseRepository.getFilmActorWithCat().map { listFilmActor ->
        (listFilmActor + FilmActorTable(
            DefaultCats.WantSee.code, -1, null,
            null, null, null,
            null, null, null
        ) + FilmActorTable(
            DefaultCats.Love.code, -1, null,
            null, null, null,
            null, null, null
        )).sortedBy { it.category }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )
    var tempFilmActorList: List<FilmActorTable>? = null
    val seeHistoryFlow = databaseRepository.getSearchHistory("0").stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )
    val searchHistoryFlow = databaseRepository.getSearchHistory("1").stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    fun emitResultFV(mode: Int, isChecked: Boolean) {
        viewModelScope.launch {
            if (mode == 1)
                _resultsF.emit(isChecked)
            else
                _resultsV.emit(isChecked)
        }
    }

    fun emitResBackDialog(userSelect: Int) {
        viewModelScope.launch {
            _resBackDialog.emit(userSelect)
        }
    }

    fun emitGenreCountry(id: Long) {
        viewModelScope.launch {
            _resGenreCountry.emit(id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanGenreCountry() {
        _resGenreCountry.resetReplayCache()
    }

    fun emitYear(years: Int) {
        viewModelScope.launch {
            _resYear.emit(years)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanYear() {
        _resYear.resetReplayCache()
    }

    fun emitNewCat(newCat: String) {
        viewModelScope.launch {
            _resAddNewCat.emit(newCat)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanNewCat() {
        _resAddNewCat.resetReplayCache()
    }

    fun emitBottomSh() {
        viewModelScope.launch {
            _resBottomSh.emit(true)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanBottomSh() {
        _resBottomSh.resetReplayCache()
    }

    fun emitDeleteCat(category: String) {
        viewModelScope.launch {
            _resDeleteCat.emit(category)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cleanDeleteCat() {
        _resDeleteCat.resetReplayCache()
    }

    fun updateFilmCat(id: Long, newCategory: List<FilmActorTable>) {
        viewModelScope.launch {
            databaseRepository.updateFilmCat(id, newCategory.map {
                if (it.kinopoiskId != -2L)
                    it
                else
                    FilmActorTable(
                        it.category, -1L, it.isActor, it.posterUrlPreview,
                        it.nameRu, it.nameEn, it.nameOriginal, it.genres, it.rating
                    )
            })
        }
    }

    fun insertFilmOrCat(filmOrCat: FilmActorTable) {
        viewModelScope.launch {
            databaseRepository.insertFilmOrCat(filmOrCat)
        }
    }

    fun insertHistoryItem(filmActorTable: FilmActorTable) {
        viewModelScope.launch {
            if (searchHistoryFlow.value.size >= 50) {
                val oldElem = searchHistoryFlow.value.minByOrNull { it.dateCreate }
                if (oldElem != null) {
                    databaseRepository.deleteFilmActor(oldElem)
                }
            }
            databaseRepository.insertFilmOrCat(filmActorTable)
        }
    }

    fun deleteFilm(filmId: Long, category: String) {
        viewModelScope.launch {
            databaseRepository.deleteFilm(filmId, category)
        }
    }

    fun deleteCategory(category: String) {
        viewModelScope.launch {
            databaseRepository.deleteCategory(category)
        }
    }
}
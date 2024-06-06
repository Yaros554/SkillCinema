package com.skyyaros.skillcinema.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyaros.skillcinema.data.DatabaseRepository
import com.skyyaros.skillcinema.data.DefaultCats
import com.skyyaros.skillcinema.data.StoreRepository
import com.skyyaros.skillcinema.entity.AppSettings
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

class MainViewModel(private val databaseRepository: DatabaseRepository, private val storeRepository: StoreRepository): ViewModel() {
    var isFullPhotoFragment = false
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
    val statusStartFlow = storeRepository.getStartStatus().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )
    val statusPhotoDialogFlow = storeRepository.getDialogStatusFlow(FullscreenDialogInfoMode.PHOTO).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )
    val statusVideoDialogFlow = storeRepository.getDialogStatusFlow(FullscreenDialogInfoMode.VIDEO).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )
    val appSettingsFlow = storeRepository.getAppSettingsFlow().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )
    val url_pos_anim = mutableMapOf(
        Pair("Home", ""),
        Pair("Search", ""),
        Pair("Profile", "")
    )

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

    fun setStartStatus(startStatus: Boolean) {
        viewModelScope.launch {
            storeRepository.setStartStatus(startStatus)
        }
    }

    fun setDialogStatus(mode: FullscreenDialogInfoMode, isShow: Boolean) {
        viewModelScope.launch {
            storeRepository.setDialogStatus(mode, isShow)
        }
    }

    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch {
            storeRepository.setAppSettings(settings)
        }
    }
}
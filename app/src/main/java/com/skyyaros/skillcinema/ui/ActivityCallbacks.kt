package com.skyyaros.skillcinema.ui

import com.skyyaros.skillcinema.entity.SearchQuery
import kotlinx.coroutines.flow.SharedFlow

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
}
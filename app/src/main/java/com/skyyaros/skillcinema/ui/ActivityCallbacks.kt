package com.skyyaros.skillcinema.ui

import kotlinx.coroutines.flow.SharedFlow

interface ActivityCallbacks {
    fun showDownBar()
    fun hideDownBar()
    fun showUpBar(label: String)
    fun hideUpBar()
    fun fullScreenOn()
    fun fullScreenOff()
    fun goToFullScreenMode(needGo: Boolean)
    fun emitResult(mode: Int, isChecked: Boolean)
    fun getResultStream(mode: Int): SharedFlow<Boolean>
}
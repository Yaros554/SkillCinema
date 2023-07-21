package com.skyyaros.skillcinema.ui

interface ActivityCallbacks {
    fun showDownBar()
    fun hideDownBar()
    fun showUpBar(label: String)
    fun hideUpBar()
    fun fullScreenOn()
    fun fullScreenOff()
    fun goToFullScreenMode(needGo: Boolean)
}
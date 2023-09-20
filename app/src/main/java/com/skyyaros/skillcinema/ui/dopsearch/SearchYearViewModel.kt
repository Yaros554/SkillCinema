package com.skyyaros.skillcinema.ui.dopsearch

import androidx.lifecycle.ViewModel
import java.util.Calendar

class SearchYearViewModel(_fromYear: Int, _toYear:Int): ViewModel() {
    var isChecked = _fromYear == 1000
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val fromYear = if (_fromYear != 1000) _fromYear else currentYear - 7
    private val toYear = if (_toYear != 3000) _toYear else currentYear + 4
    private val minYear = currentYear - 7 - 12 * ((currentYear - 7 - 1884)/12 + 1)
    private val maxYear = currentYear + 4 + 12
    val years = List((maxYear + 1 - minYear) / 12) { page ->
        List(12) {
            minYear + page * 12 + it
        }
    }
    var currentPageFrom = years.indexOfFirst { it.contains(fromYear) }
    var currentIndexFrom = years[currentPageFrom].indexOfFirst { it == fromYear }
    var currentPageTo = years.indexOfFirst { it.contains(toYear) }
    var currentIndexTo = years[currentPageTo].indexOfFirst { it == toYear }
    val currentYearFrom get() = years[currentPageFrom][currentIndexFrom]
    val currentYearTo get() = years[currentPageTo][currentIndexTo]
}
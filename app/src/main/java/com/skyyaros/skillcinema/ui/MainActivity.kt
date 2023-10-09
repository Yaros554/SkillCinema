package com.skyyaros.skillcinema.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.ActivityMainBinding
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.ui.hello.HelloFragment
import com.skyyaros.skillcinema.ui.search.SetSearchFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ActivityCallbacks {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(App.component.getDatabaseRepository()) as T
            }
        }
    }
    private var myCoroutine: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.popBackStack(R.id.homeFragment, inclusive = false)
                }
                R.id.search -> {
                    navController.popBackStack(R.id.searchFragment, inclusive = false)
                }
                else -> {
                    navController.popBackStack(R.id.personFragment, inclusive = false)
                }
            }
        }
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.searchFragment, R.id.personFragment, R.id.helloFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            binding.bottomNav.elevation = 1.0F
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isFullPhotoFragment) {
            val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
            val pToolbar = binding.toolbar.layoutParams as ViewGroup.MarginLayoutParams
            pToolbar.setMargins(0, statusBarHeight, 0, 0)
            binding.toolbar.requestLayout()
            val pContainer = binding.navHostFragment.layoutParams as ViewGroup.MarginLayoutParams
            pContainer.setMargins(0, statusBarHeight, 0, 0)
            binding.navHostFragment.requestLayout()
            val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            window.decorView.systemUiVisibility = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            else
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (SetSearchFragment.backPressedListener != null) {
            val did = SetSearchFragment.backPressedListener!!.onBackPressed()
            if (!did)
                navController.navigateUp(appBarConfiguration)
            else
                false
        } else {
            navController.navigateUp(appBarConfiguration)
        }
    }

    override fun onBackPressed() {
        if (HelloFragment.backPressedListener != null)
            HelloFragment.backPressedListener!!.onBackPressed()
        else if (SetSearchFragment.backPressedListener != null) {
            val did = SetSearchFragment.backPressedListener!!.onBackPressed()
            if (!did)
                super.onBackPressed()
        } else
            super.onBackPressed()
    }

    override fun showDownBar() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    override fun hideDownBar() {
        binding.bottomNav.visibility = View.GONE
    }

    override fun showUpBar(label: String) {
        binding.toolbar.visibility = View.VISIBLE
        binding.toolbar.title = ""
        binding.toolbarTitle.text = label
        binding.toolbar.setNavigationIcon(R.drawable.icon_back)
    }

    override fun hideUpBar() {
        binding.toolbar.visibility = View.GONE
    }

    override fun goToFullScreenMode(needGo: Boolean) {
        val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
        val pToolbar = binding.toolbar.layoutParams as ViewGroup.MarginLayoutParams
        pToolbar.setMargins(0, statusBarHeight, 0, 0)
        binding.toolbar.requestLayout()
        val pContainer = binding.navHostFragment.layoutParams as ViewGroup.MarginLayoutParams
        if (needGo) {
            val a = TypedValue()
            theme.resolveAttribute(android.R.attr.windowBackground, a, true)
            binding.toolbar.setBackgroundColor(a.data)
            binding.bottomNav.visibility = View.GONE
            pContainer.setMargins(0, 0, 0, 0)
        } else {
            binding.toolbar.background = null
            val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            window.decorView.systemUiVisibility = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            else
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            binding.bottomNav.visibility = View.VISIBLE
            pContainer.setMargins(0, statusBarHeight, 0, 0)
        }
        binding.navHostFragment.requestLayout()
        viewModel.isFullPhotoFragment = needGo
    }

    override fun fullScreenOn() {
        myCoroutine?.cancel()
        myCoroutine = lifecycleScope.launch {
            binding.toolbar.visibility = View.GONE
            delay(300)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    override fun fullScreenOff() {
        myCoroutine?.cancel()
        myCoroutine = lifecycleScope.launch {
            val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            window.decorView.systemUiVisibility = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            else
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            delay(300)
            binding.toolbar.visibility = View.VISIBLE
        }
    }

    override fun emitResultFV(mode: Int, isChecked: Boolean) {
        viewModel.emitResultFV(mode, isChecked)
    }

    override fun getResultStreamFV(mode: Int): SharedFlow<Boolean> {
        return if (mode == 1)
            viewModel.resultF
        else
            viewModel.resultV
    }

    override fun emitResBackDialog(userSelect: Int) {
        viewModel.emitResBackDialog(userSelect)
    }

    override fun getResStreamBackDialog(): SharedFlow<Int> {
        return viewModel.resBackDialog
    }

    override fun emitGenreCountry(id: Long) {
        viewModel.emitGenreCountry(id)
    }

    override fun getGenreCountryFlow(): SharedFlow<Long> {
        return viewModel.resGenreCountry
    }

    override fun cleanGenreCountry() {
        viewModel.cleanGenreCountry()
    }

    override fun emitYear(years: Int) {
        viewModel.emitYear(years)
    }

    override fun getYearFlow(): SharedFlow<Int> {
        return viewModel.resYear
    }

    override fun cleanYear() {
        viewModel.cleanYear()
    }

    override fun emitNewCat(newCat: String) {
        viewModel.emitNewCat(newCat)
    }

    override fun getNewCatFlow(): SharedFlow<String> {
        return viewModel.resAddNewCat
    }

    override fun cleanNewCat() {
        viewModel.cleanNewCat()
    }

    override fun emitBottomSh() {
        viewModel.emitBottomSh()
    }

    override fun cleanBottomSh() {
        viewModel.cleanBottomSh()
    }

    override fun getBottomShFlow(): SharedFlow<Boolean> {
        return viewModel.resBottomSh
    }

    override fun emitDeleteCat(category: String) {
        viewModel.emitDeleteCat(category)
    }

    override fun cleanDeleteCat() {
        viewModel.cleanDeleteCat()
    }

    override fun getDeleteCatFlow(): SharedFlow<String> {
        return viewModel.resDeleteCat
    }

    override fun getSearchQuery(): SearchQuery {
        return viewModel.searchQuery
    }

    override fun setSearchQuery(searchQuery: SearchQuery) {
        viewModel.searchQuery = searchQuery
    }

    override fun getActorFilmWithCatFlow(): StateFlow<List<FilmActorTable>> {
        return viewModel.filmActorWithCatFlow
    }

    override fun getTempFilmActorList(): List<FilmActorTable>? {
        return viewModel.tempFilmActorList
    }

    override fun getSearchHistoryFlow(): StateFlow<List<FilmActorTable>> {
        return viewModel.searchHistoryFlow
    }

    override fun getSeeHistoryFlow(): StateFlow<List<FilmActorTable>> {
        return viewModel.seeHistoryFlow
    }

    override fun setTempFilmActorList(newList: List<FilmActorTable>?) {
        viewModel.tempFilmActorList = newList
    }

    override fun updateFilmCat(id: Long, newCategory: List<FilmActorTable>) {
        viewModel.updateFilmCat(id, newCategory)
    }

    override fun insertFilmOrCat(filmOrCat: FilmActorTable) {
        viewModel.insertFilmOrCat(filmOrCat)
    }

    override fun insertHistoryItem(filmActorTable: FilmActorTable) {
        viewModel.insertHistoryItem(filmActorTable)
    }

    override fun deleteFilm(filmId: Long, category: String) {
        viewModel.deleteFilm(filmId, category)
    }

    override fun deleteCategory(category: String) {
        viewModel.deleteCategory(category)
    }
}
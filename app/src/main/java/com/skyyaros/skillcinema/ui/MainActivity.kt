package com.skyyaros.skillcinema.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.ActivityMainBinding
import com.skyyaros.skillcinema.ui.hello.HelloFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ActivityCallbacks {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()
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
            goToFullScreenMode(viewModel.isFullPhotoFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onBackPressed() {
        HelloFragment.backPressedListener?.onBackPressed() ?: super.onBackPressed()
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

    override fun emitResult(mode: Int, isChecked: Boolean) {
        viewModel.emitResult(mode, isChecked)
    }

    override fun getResultStream(mode: Int): SharedFlow<Boolean> {
        return if (mode == 1)
            viewModel.resultF
        else
            viewModel.resultV
    }
}
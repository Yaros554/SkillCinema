package com.skyyaros.skillcinema.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.ActivityMainBinding
import com.skyyaros.skillcinema.ui.hello.HelloFragment

class MainActivity : AppCompatActivity(), ActivityCallbacks {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(App.component.getKinopoiskRepository()) as T
            }
        }
    }

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
        viewModel.isInit = true
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            binding.bottomNav.elevation = 1.0F
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

    override fun getMainViewModel(): MainViewModel {
        return viewModel
    }
}
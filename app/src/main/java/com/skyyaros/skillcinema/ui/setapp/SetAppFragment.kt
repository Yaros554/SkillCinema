package com.skyyaros.skillcinema.ui.setapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SetAppFragmentBinding
import com.skyyaros.skillcinema.entity.AppSettings
import com.skyyaros.skillcinema.entity.AppTheme
import com.skyyaros.skillcinema.entity.VideoSource
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.BackDialogResult
import com.skyyaros.skillcinema.ui.BackDialogViewModel
import com.skyyaros.skillcinema.ui.BackPressedListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

class SetAppFragment: Fragment(), BackPressedListener {
    private var _bind: SetAppFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private var fragmentWillKill = false
    private val sharedViewModel: BackDialogViewModel by activityViewModels()
    private var isFirst: Boolean = true
    private val keyIsFirst = "KEYISFIRST"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = SetAppFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = savedInstanceState?.getBoolean(keyIsFirst, true) ?: true
        bind.chipAll.setOnClickListener {
            sharedViewModel.curSource = VideoSource.ANY
            bind.chipAll.isChecked = true
            bind.chipKinopoisk.isChecked = false
            bind.chipYoutube.isChecked = false
        }
        bind.chipKinopoisk.setOnClickListener {
            sharedViewModel.curSource = VideoSource.KINOPOISK
            bind.chipAll.isChecked = false
            bind.chipKinopoisk.isChecked = true
            bind.chipYoutube.isChecked = false
        }
        bind.chipYoutube.setOnClickListener {
            sharedViewModel.curSource = VideoSource.YOUTUBE
            bind.chipAll.isChecked = false
            bind.chipKinopoisk.isChecked = false
            bind.chipYoutube.isChecked = true
        }

        bind.chipAuto.setOnClickListener {
            sharedViewModel.curTheme = AppTheme.AUTO
            bind.chipAuto.isChecked = true
            bind.chipLight.isChecked = false
            bind.chipDark.isChecked = false
        }
        bind.chipLight.setOnClickListener {
            sharedViewModel.curTheme = AppTheme.DAY
            bind.chipAuto.isChecked = false
            bind.chipLight.isChecked = true
            bind.chipDark.isChecked = false
        }
        bind.chipDark.setOnClickListener {
            sharedViewModel.curTheme = AppTheme.NIGHT
            bind.chipAuto.isChecked = false
            bind.chipLight.isChecked = false
            bind.chipDark.isChecked = true
        }
        bind.switchAnim.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.animActive = isChecked
        }

        bind.button.setOnClickListener {
            val newSettings = AppSettings(
                sharedViewModel.curSource,
                sharedViewModel.curTheme,
                sharedViewModel.animActive
            )
            if (newSettings != activityCallbacks!!.getAppSettingsFlow().value)
                activityCallbacks!!.saveSettings(newSettings)
            fragmentWillKill = true
            backPressedListener = null
            requireActivity().onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getAppSettingsFlow().collect { settings ->
                if (settings == null) {
                    hideShowUI(View.GONE)
                } else {
                    hideShowUI(View.VISIBLE)
                    if (isFirst) {
                        when (settings.videoSource) {
                            VideoSource.ANY -> {
                                bind.chipAll.isChecked = true
                                bind.chipKinopoisk.isChecked = false
                                bind.chipYoutube.isChecked = false
                            }
                            VideoSource.YOUTUBE -> {
                                bind.chipAll.isChecked = false
                                bind.chipKinopoisk.isChecked = false
                                bind.chipYoutube.isChecked = true
                            }
                            else -> {
                                bind.chipAll.isChecked = false
                                bind.chipKinopoisk.isChecked = true
                                bind.chipYoutube.isChecked = false
                            }
                        }
                        sharedViewModel.curSource = settings.videoSource
                        when (settings.theme) {
                            AppTheme.AUTO -> {
                                bind.chipAuto.isChecked = true
                                bind.chipLight.isChecked = false
                                bind.chipDark.isChecked = false
                            }
                            AppTheme.DAY -> {
                                bind.chipAuto.isChecked = false
                                bind.chipLight.isChecked = true
                                bind.chipDark.isChecked = false
                            }
                            else -> {
                                bind.chipAuto.isChecked = false
                                bind.chipLight.isChecked = false
                                bind.chipDark.isChecked = true
                            }
                        }
                        sharedViewModel.curTheme = settings.theme
                        bind.switchAnim.isChecked = settings.animActive
                        sharedViewModel.animActive = settings.animActive
                        isFirst = false
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.resBackDialog.collect {
                sharedViewModel.cleanResBackDialog()
                when (it) {
                    BackDialogResult.OK -> {
                        val newSettings = AppSettings(
                            sharedViewModel.curSource,
                            sharedViewModel.curTheme,
                            sharedViewModel.animActive
                        )
                        activityCallbacks!!.saveSettings(newSettings)
                        fragmentWillKill = true
                        backPressedListener = null
                        requireActivity().onBackPressed()
                    }
                    BackDialogResult.NO -> {
                        fragmentWillKill = true
                        backPressedListener = null
                        requireActivity().onBackPressed()
                    }
                    else -> {
                        delay(50) //костыль, чтобы не сбивался toolbar
                        activityCallbacks!!.showUpBar(getString(R.string.set_app_title))
                    }
                }
            }
        }
    }

    private fun hideShowUI(hideShow: Int) {
        bind.sourceText.visibility = hideShow
        bind.chipAll.visibility = hideShow
        bind.chipYoutube.visibility = hideShow
        bind.chipKinopoisk.visibility = hideShow
        bind.themeText.visibility = hideShow
        bind.chipAuto.visibility = hideShow
        bind.chipLight.visibility = hideShow
        bind.chipDark.visibility = hideShow
        bind.switchAnim.visibility = hideShow
        bind.button.visibility = hideShow
        bind.progressBar.visibility = if (hideShow == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        activityCallbacks!!.showUpBar(getString(R.string.set_app_title))
        if (!fragmentWillKill)
            backPressedListener = this
    }

    override fun onPause() {
        backPressedListener = null
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(keyIsFirst, isFirst)
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }

    override fun onBackPressed(): Boolean {
        val newSource = when {
            bind.chipAll.isChecked -> VideoSource.ANY
            bind.chipKinopoisk.isChecked -> VideoSource.KINOPOISK
            else -> VideoSource.YOUTUBE
        }
        val newTheme = when {
            bind.chipAuto.isChecked -> AppTheme.AUTO
            bind.chipLight.isChecked -> AppTheme.DAY
            else -> AppTheme.NIGHT
        }
        val anim = bind.switchAnim.isChecked
        val newSettings = AppSettings(newSource, newTheme, anim)
        val oldSettings = activityCallbacks!!.getAppSettingsFlow().value
        return if (newSettings != oldSettings) {
            val action = SetAppFragmentDirections.actionSetAppFragmentToBackDialog()
            findNavController().navigate(action)
            true
        } else {
            false
        }
    }

    companion object {
        var backPressedListener: BackPressedListener? = null
    }
}
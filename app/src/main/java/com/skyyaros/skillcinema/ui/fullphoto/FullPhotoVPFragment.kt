package com.skyyaros.skillcinema.ui.fullphoto

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.databinding.FullPhotoVpFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale

class FullPhotoVPFragment: Fragment() {
    private var _bind: FullPhotoVpFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: FullPhotoVPFragmentArgs by navArgs()
    private val viewModel: FullPhotoVPViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FullPhotoVPViewModel(
                    args.urls.toList(), args.title,
                    args.id, App.component.getKinopoiskRepository(),
                    (args.urls.toList().indexOfFirst { it.imageUrl == args.curUrl }) / 20 + 1
                ) as T
            }
        }
    }
    private val onClick = {
        viewModel.fullScreen = !viewModel.fullScreen
        if (viewModel.fullScreen)
            activityCallbacks!!.fullScreenOn()
        else
            activityCallbacks!!.fullScreenOff()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = FullPhotoVpFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val types = mapOf(
            Pair("STILL", "КАДРЫ"),
            Pair("SHOOTING", "СО СЪЕМОК"),
            Pair("POSTER", "ПОСТЕРЫ"),
            Pair("FAN_ART", "ФАН-АРТЫ"),
            Pair("PROMO", "ПРОМО"),
            Pair("CONCEPT", "КОНЦЕПТ-АРТЫ"),
            Pair("WALLPAPER", "ОБОИ"),
            Pair("COVER", "ОБЛОЖКИ"),
            Pair("SCREENSHOT", "СКРИНШОТЫ"),
            Pair("NO CATEGORY", "НЕТ КАТЕГОРИИ")
        )
        val type = if (Locale.getDefault().language == "ru")
            types[args.title] ?: args.title
        else
            args.title
        activityCallbacks!!.showUpBar(type.lowercase().replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase()
            else
                it.toString() })
        activityCallbacks!!.goToFullScreenMode(true)
        if (args.id == -1L) {
            val adapter = FullPhotoAdapterNoPaging(args.urls.toList(), requireContext(), onClick)
            bind.viewPager.adapter = adapter
            val index = args.urls.toList().indexOfFirst { it.imageUrl == args.curUrl }
            bind.viewPager.setCurrentItem(index, false)
        } else {
            val adapter = FullPhotoAdapter(requireContext(), onClick)
            bind.viewPager.adapter = adapter.withLoadStateFooter(FullPhotoLoadStateAdapter( {
                adapter.retry()
            }, onClick))
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.pagingPhotos!!.collect {
                    adapter.submitData(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                adapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect {
                        if (viewModel.isFirst) {
                            val index = adapter.snapshot().items.indexOfFirst { it.imageUrl == args.curUrl }
                            bind.viewPager.setCurrentItem(index, false)
                            viewModel.isFirst = false
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.fullScreen)
            activityCallbacks!!.fullScreenOn()
        else
            activityCallbacks!!.fullScreenOff()
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}
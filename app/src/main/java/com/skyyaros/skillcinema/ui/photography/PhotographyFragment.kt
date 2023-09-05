package com.skyyaros.skillcinema.ui.photography

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotographyFragmentBinding
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlinx.coroutines.delay
import java.util.*

class PhotographyFragment: Fragment() {
    private var _bind: PhotographyFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: PhotographyFragmentArgs by navArgs()
    val viewModel: PhotographyViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhotographyViewModel(
                    args.data.toList(), args.id,
                    App.component.getKinopoiskRepository(),
                    App.component.getStoreRepository()
                ) as T
            }
        }
    }
    val goToPhotos: (String, List<ImageItem>, String)->Unit = { title, urls, curUrl ->
        viewModel.title = title
        viewModel.urls = urls
        viewModel.curUrl = curUrl
        val status = viewModel.statusPhotoDialogFlow.value
        if (status != 2) {
            val action = PhotographyFragmentDirections.actionPhotographyFragmentToFullscreenDialogInfo(
                    1,
                    status == 0
                )
            findNavController().navigate(action)
        } else {
            val action = PhotographyFragmentDirections.actionPhotographyFragmentToFullPhotoVPFragment(
                viewModel.title,
                viewModel.urls.toTypedArray(),
                viewModel.curUrl, args.id
            )
            findNavController().navigate(action)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = PhotographyFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.goToFullScreenMode(false)
        activityCallbacks!!.showUpBar(getString(R.string.detail_text_gallery))
        val items = args.data.toList()
        val adapter = PhotographyItemAdapter(items, this)
        bind.viewPager.adapter = adapter
        TabLayoutMediator(bind.tabs, bind.viewPager) { tab, position ->
            val types = mapOf(
                Pair("STILL", "КАДРЫ"),
                Pair("SHOOTING", "СО СЪЕМОК"),
                Pair("POSTER", "ПОСТЕРЫ"),
                Pair("FAN_ART", "ФАН-АРТЫ"),
                Pair("PROMO", "ПРОМО"),
                Pair("CONCEPT", "КОНЦЕПТ-АРТЫ"),
                Pair("WALLPAPER", "ОБОИ"),
                Pair("COVER", "ОБЛОЖКИ"),
                Pair("SCREENSHOT", "СКРИНШОТЫ")
            )
            val type = if (Locale.getDefault().language == "ru")
                types[items[position].imageType!!] ?: items[position].imageType!!
            else
                items[position].imageType!!
            tab.text = "${type.lowercase().replaceFirstChar { 
                if (it.isLowerCase()) 
                    it.titlecase() 
                else 
                    it.toString() }
            } ${items[position].total}"
        }.attach()
        for (i in 0 until bind.tabs.tabCount) {
            val tab = (bind.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            if (i == 0) {
                p.setMargins(16, 0, 16, 0)
            } else {
                p.setMargins(0, 0, 16, 0)
            }
            tab.requestLayout()
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getResultStreamFV(1).collect { isChecked ->
                viewModel.setDialogStatus(if (isChecked) 2 else 1)
                val action = PhotographyFragmentDirections.actionPhotographyFragmentToFullPhotoVPFragment(
                    viewModel.title,
                    viewModel.urls.toTypedArray(),
                    viewModel.curUrl, args.id
                )
                while ((findNavController().currentDestination?.label ?: "") != "PhotographyFragment")
                    delay(1)
                findNavController().navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityCallbacks!!.showUpBar(getString(R.string.detail_text_gallery))
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
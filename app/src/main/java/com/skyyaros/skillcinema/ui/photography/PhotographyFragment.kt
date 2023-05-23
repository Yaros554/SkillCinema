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
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotographyFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class PhotographyFragment: Fragment() {
    private var _bind: PhotographyFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: PhotographyFragmentArgs by navArgs()
    val viewModel: PhotographyViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhotographyViewModel(args.data.toList(), args.id, App.component.getKinopoiskRepository()) as T
            }
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
        activityCallbacks!!.showUpBar(getString(R.string.detail_text_gallery))
        val items = args.data.toList()
        val adapter = PhotographyItemAdapter(items, this)
        bind.viewPager.adapter = adapter
        TabLayoutMediator(bind.tabs, bind.viewPager) { tab, position ->
            tab.text = "${items[position].imageType!!} ${items[position].total}"
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
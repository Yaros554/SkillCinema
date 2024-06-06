package com.skyyaros.skillcinema.ui.series

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.skyyaros.skillcinema.databinding.SeriesFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlin.math.abs

class SeriesFragment: Fragment() {
    private var _bind: SeriesFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: SeriesFragmentArgs by navArgs()
    private val mAnimator = ViewPager2.PageTransformer { page, position ->
        val absPos = abs(position)
        page.apply {
            rotation = position * 360
            translationY = absPos * 500f
            val scale = if (absPos > 1) 0F else 1 - absPos
            scaleX = scale
            scaleY = scale
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = SeriesFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.showUpBar(args.name)
        if (activityCallbacks!!.getAppSettingsFlow().value?.animActive != false)
            bind.viewPager.setPageTransformer(mAnimator)
        val adapter = SeriesItemAdapter(args.data.toList(), requireContext())
        bind.viewPager.adapter = adapter
        TabLayoutMediator(bind.tabs, bind.viewPager) { tab, position ->
            tab.text = args.data.toList()[position].number.toString()
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
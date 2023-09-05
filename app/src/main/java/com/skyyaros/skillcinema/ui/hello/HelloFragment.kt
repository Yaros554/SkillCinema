package com.skyyaros.skillcinema.ui.hello

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.HelloFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.BackPressedListener

class HelloFragment: Fragment(), BackPressedListener {
    private var _bind: HelloFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = HelloFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataList = listOf(
            HelloFragmentData(R.drawable.hello_1, getString(R.string.hello_premiere)),
            HelloFragmentData(R.drawable.hello_3, getString(R.string.hello_collection)),
            HelloFragmentData(R.drawable.hello_2, getString(R.string.hello_friends))
        )
        val adapter = ViewPagerAdapter(dataList)
        bind.viewPager.adapter = adapter
        bind.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        bind.skip.text = getString(R.string.hello_skip)
                        bind.imagePoints.setImageResource(R.drawable.three_points_1)
                    }
                    1 -> {
                        bind.skip.text = getString(R.string.hello_skip)
                        bind.imagePoints.setImageResource(R.drawable.three_points_2)
                    }
                    2 -> {
                        bind.skip.text = getString(R.string.hello_start)
                        bind.imagePoints.setImageResource(R.drawable.three_points_3)
                    }
                    else -> {
                        bind.skip.text = "Error!"
                    }
                }
            }
        })
        bind.skip.setOnClickListener {
            val action = HelloFragmentDirections.actionHelloFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        activityCallbacks!!.hideUpBar()
        activityCallbacks!!.hideDownBar()
    }

    override fun onResume() {
        super.onResume()
        backPressedListener = this
    }

    override fun onPause() {
        backPressedListener = null
        super.onPause()
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
        val action = HelloFragmentDirections.actionHelloFragmentToHomeFragment()
        findNavController().navigate(action)
        return true
    }

    data class HelloFragmentData(
        val imageRes: Int,
        val text: String
    )

    companion object {
        var backPressedListener: BackPressedListener? = null
    }
}
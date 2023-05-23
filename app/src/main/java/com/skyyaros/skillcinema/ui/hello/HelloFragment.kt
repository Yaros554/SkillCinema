package com.skyyaros.skillcinema.ui.hello

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.HelloFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks

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
            HelloFragmentData("hello_1", getString(R.string.hello_premiere), "three_points_1", false),
            HelloFragmentData("hello_3", getString(R.string.hello_collection), "three_points_2", false),
            HelloFragmentData("hello_2", getString(R.string.hello_friends), "three_points_3", true)
        )
        val adapter = ViewPagerAdapter(dataList, requireContext()) {
            val action = HelloFragmentDirections.actionHelloFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        bind.viewPager.adapter = adapter
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

    override fun onBackPressed() {
        val action = HelloFragmentDirections.actionHelloFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    data class HelloFragmentData(
        val image: String,
        val text: String,
        val imagePoints: String,
        val isFinish: Boolean
    )

    companion object {
        var backPressedListener: BackPressedListener? = null
    }
}
package com.skyyaros.skillcinema.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SetSearchFragmentBinding
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.BackPressedListener
import kotlinx.coroutines.delay

class SetSearchFragment: Fragment(), BackPressedListener {
    private var activityCallbacks: ActivityCallbacks? = null
    private var _bind: SetSearchFragmentBinding? = null
    private val bind get() = _bind!!
    private lateinit var oldQuery: SearchQuery
    private var fragmentWillKill = false
    private val viewModel: SetSearchViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
        oldQuery = activityCallbacks!!.getSearchQuery()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = SetSearchFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.initState)
            initCurrentSet()
        bind.button.setOnClickListener {
            val newQuery = SearchQuery(
                null, null,
                "RATING", "ALL",
                bind.rangeSlider.values.min().toInt(),
                bind.rangeSlider.values.max().toInt(),
                1000, 3000, oldQuery.keyword
            )
            if (oldQuery != newQuery) {
                activityCallbacks!!.setSearchQuery(newQuery)
                val action = SetSearchFragmentDirections.actionSetSearchFragmentToSearchFragment()
                findNavController().navigate(action)
            } else {
                fragmentWillKill = true
                backPressedListener = null
                requireActivity().onBackPressed()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getResStreamBackDialog().collect {
                if (it == 1) {
                    val newQuery = SearchQuery(
                        null, null,
                        "RATING", "ALL",
                        bind.rangeSlider.values.min().toInt(),
                        bind.rangeSlider.values.max().toInt(),
                        1000, 3000, oldQuery.keyword
                    )
                    activityCallbacks!!.setSearchQuery(newQuery)
                    val action = SetSearchFragmentDirections.actionSetSearchFragmentToSearchFragment()
                    while ((findNavController().currentDestination?.label ?: "") != "SetSearchFragment")
                        delay(1)
                    findNavController().navigate(action)
                } else if (it == 0) {
                    fragmentWillKill = true
                    backPressedListener = null
                    requireActivity().onBackPressed()
                } else {
                    delay(50) //костыль, чтобы не сбивался toolbar
                    activityCallbacks!!.showUpBar(getString(R.string.search_set_title))
                }
            }
        }
    }

    private fun initCurrentSet() {
        bind.rangeSlider.setValues(oldQuery.ratingFrom.toFloat(), oldQuery.ratingTo.toFloat())
        viewModel.initState = false
    }

    override fun onResume() {
        super.onResume()
        activityCallbacks!!.showUpBar(getString(R.string.search_set_title))
        if (!fragmentWillKill)
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
        val oldQuery = activityCallbacks!!.getSearchQuery()
        val newQuery = SearchQuery(
            null, null,
            "RATING", "ALL",
            bind.rangeSlider.values.min().toInt(),
            bind.rangeSlider.values.max().toInt(),
            1000, 3000, oldQuery.keyword
        )
        if (oldQuery == newQuery)
            return false
        else {
            val action = SetSearchFragmentDirections.actionSetSearchFragmentToBackDialog()
            findNavController().navigate(action)
            return true
        }
    }

    companion object {
        var backPressedListener: BackPressedListener? = null
    }
}
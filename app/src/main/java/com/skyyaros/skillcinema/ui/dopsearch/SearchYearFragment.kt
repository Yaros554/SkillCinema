package com.skyyaros.skillcinema.ui.dopsearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SearchYearFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class SearchYearFragment: Fragment() {
    private var _bind: SearchYearFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: SearchYearFragmentArgs by navArgs()
    private val viewModel: SearchYearViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchYearViewModel(args.yearFrom, args.yearTo) as T
            }
        }
    }
    private val pageChangeCallbackFrom = MyOnPageChangeCallback(1)
    private val pageChangeCallbackTo = MyOnPageChangeCallback(2)
    val getCurrentIndex: (Int)->Int = { mode ->
        if (mode == 1)
            viewModel.currentIndexFrom
        else
            viewModel.currentIndexTo
    }
    val setCurrentIndex: (Int, Int)->Unit = { index, mode ->
        if (mode == 1)
            viewModel.currentIndexFrom = index
        else
            viewModel.currentIndexTo = index
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = SearchYearFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.showUpBar(getString(R.string.search_set_year_title))
        val adapterFrom = YearAdapter(viewModel.years,1, this)
        bind.viewPagerFrom.adapter = adapterFrom
        bind.viewPagerFrom.setCurrentItem(viewModel.currentPageFrom, false)
        bind.viewPagerFrom.registerOnPageChangeCallback(pageChangeCallbackFrom)
        val adapterTo = YearAdapter(viewModel.years,2, this)
        bind.viewPagerTo.adapter = adapterTo
        bind.viewPagerTo.setCurrentItem(viewModel.currentPageTo, false)
        bind.viewPagerTo.registerOnPageChangeCallback(pageChangeCallbackTo)
        bind.yearFrom.text = "${viewModel.years[viewModel.currentPageFrom][0]}-${viewModel.years[viewModel.currentPageFrom][11]}"
        bind.yearTo.text = "${viewModel.years[viewModel.currentPageTo][0]}-${viewModel.years[viewModel.currentPageTo][11]}"
        if (viewModel.isChecked) {
            bind.checkBox.isChecked = true
            bind.searchTextFrom.visibility = View.GONE
            bind.frameLayoutFrom.visibility = View.GONE
            bind.searchTextTo.visibility = View.GONE
            bind.frameLayoutTo.visibility = View.GONE
        } else {
            bind.checkBox.isChecked = false
            bind.searchTextFrom.visibility = View.VISIBLE
            bind.frameLayoutFrom.visibility = View.VISIBLE
            bind.searchTextTo.visibility = View.VISIBLE
            bind.frameLayoutTo.visibility = View.VISIBLE
        }

        bind.imageBackFrom.setOnClickListener {
            viewModel.currentPageFrom--
            bind.viewPagerFrom.setCurrentItem(viewModel.currentPageFrom, true)
            bind.yearFrom.text = "${viewModel.years[viewModel.currentPageFrom][0]}-${viewModel.years[viewModel.currentPageFrom][11]}"
            if (viewModel.currentPageFrom == 0) {
                bind.imageBackFrom.visibility = View.GONE
            }
            if (viewModel.currentPageFrom == viewModel.years.lastIndex - 1) {
                bind.imageForwardFrom.visibility = View.VISIBLE
            }
        }
        bind.imageForwardFrom.setOnClickListener {
            viewModel.currentPageFrom++
            bind.viewPagerFrom.setCurrentItem(viewModel.currentPageFrom, true)
            bind.yearFrom.text = "${viewModel.years[viewModel.currentPageFrom][0]}-${viewModel.years[viewModel.currentPageFrom][11]}"
            if (viewModel.currentPageFrom == viewModel.years.lastIndex) {
                bind.imageForwardFrom.visibility = View.GONE
            }
            if (viewModel.currentPageFrom == 1) {
                bind.imageBackFrom.visibility = View.VISIBLE
            }
        }
        bind.imageBackTo.setOnClickListener {
            viewModel.currentPageTo--
            bind.viewPagerTo.setCurrentItem(viewModel.currentPageTo, true)
            bind.yearTo.text = "${viewModel.years[viewModel.currentPageTo][0]}-${viewModel.years[viewModel.currentPageTo][11]}"
            if (viewModel.currentPageTo == 0) {
                bind.imageBackTo.visibility = View.GONE
            }
            if (viewModel.currentPageTo == viewModel.years.lastIndex - 1) {
                bind.imageForwardTo.visibility = View.VISIBLE
            }
        }
        bind.imageForwardTo.setOnClickListener {
            viewModel.currentPageTo++
            bind.viewPagerTo.setCurrentItem(viewModel.currentPageTo, true)
            bind.yearTo.text = "${viewModel.years[viewModel.currentPageTo][0]}-${viewModel.years[viewModel.currentPageTo][11]}"
            if (viewModel.currentPageTo == viewModel.years.lastIndex) {
                bind.imageForwardTo.visibility = View.GONE
            }
            if (viewModel.currentPageTo == 1) {
                bind.imageBackTo.visibility = View.VISIBLE
            }
        }
        bind.checkBox.setOnClickListener {
            if (bind.checkBox.isChecked) {
                bind.searchTextFrom.visibility = View.GONE
                bind.frameLayoutFrom.visibility = View.GONE
                bind.searchTextTo.visibility = View.GONE
                bind.frameLayoutTo.visibility = View.GONE
            } else {
                bind.searchTextFrom.visibility = View.VISIBLE
                bind.frameLayoutFrom.visibility = View.VISIBLE
                bind.searchTextTo.visibility = View.VISIBLE
                bind.frameLayoutTo.visibility = View.VISIBLE
            }
            viewModel.isChecked = bind.checkBox.isChecked
        }
        bind.button.setOnClickListener {
            if (bind.checkBox.isChecked) {
                activityCallbacks!!.emitYear(1000 * 10000 + 3000)
                requireActivity().onBackPressed()
            } else {
                if (viewModel.currentYearTo < viewModel.currentYearFrom) {
                    Toast.makeText(requireContext(), getString(R.string.search_set_year_toast), Toast.LENGTH_LONG).show()
                } else {
                    activityCallbacks!!.emitYear(viewModel.currentYearFrom * 10000 + viewModel.currentYearTo)
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    override fun onDestroyView() {
        bind.viewPagerFrom.unregisterOnPageChangeCallback(pageChangeCallbackFrom)
        bind.viewPagerTo.unregisterOnPageChangeCallback(pageChangeCallbackTo)
        _bind = null
        super.onDestroyView()
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }

    private inner class MyOnPageChangeCallback(private val mode: Int): ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (mode == 1) {
                viewModel.currentPageFrom = position
                bind.yearFrom.text = "${viewModel.years[viewModel.currentPageFrom][0]}-${viewModel.years[viewModel.currentPageFrom][11]}"
                if (viewModel.currentPageFrom == 0) {
                    bind.imageBackFrom.visibility = View.GONE
                }
                if (viewModel.currentPageFrom == viewModel.years.lastIndex - 1) {
                    bind.imageForwardFrom.visibility = View.VISIBLE
                }
                if (viewModel.currentPageFrom == viewModel.years.lastIndex) {
                    bind.imageForwardFrom.visibility = View.GONE
                }
                if (viewModel.currentPageFrom == 1) {
                    bind.imageBackFrom.visibility = View.VISIBLE
                }
            } else {
                viewModel.currentPageTo = position
                bind.yearTo.text = "${viewModel.years[viewModel.currentPageTo][0]}-${viewModel.years[viewModel.currentPageTo][11]}"
                if (viewModel.currentPageTo == 0) {
                    bind.imageBackTo.visibility = View.GONE
                }
                if (viewModel.currentPageTo == viewModel.years.lastIndex - 1) {
                    bind.imageForwardTo.visibility = View.VISIBLE
                }
                if (viewModel.currentPageTo == viewModel.years.lastIndex) {
                    bind.imageForwardTo.visibility = View.GONE
                }
                if (viewModel.currentPageTo == 1) {
                    bind.imageBackTo.visibility = View.VISIBLE
                }
            }
        }
    }
}
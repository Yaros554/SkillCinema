package com.skyyaros.skillcinema.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SearchFragmentBinding
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.filmography.FilmPreviewTwoAdapter

class SearchFragment: Fragment() {
    private var _bind: SearchFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val viewModel: SearchViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(App.component.getKinopoiskRepository(), activityCallbacks!!.getSearchQuery()) as T
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = SearchFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.hideUpBar()
        val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.small_margin).toInt(), false)
        val adapter = FilmPreviewTwoAdapter(requireContext()) { id ->
            val action = SearchFragmentDirections.actionSearchFragmentToDetailFilmFragment(id)
            findNavController().navigate(action)
        }
        bind.recyclerView.adapter = adapter
        if (bind.recyclerView.itemDecorationCount == 0) {
            bind.recyclerView.addItemDecoration(itemMargin)
        }
        if (bind.searchView.text.isNullOrEmpty() && activityCallbacks!!.getSearchQuery().keyword != null) {
            bind.searchView.setText(activityCallbacks!!.getSearchQuery().keyword!!)
        }
        bind.searchSettings.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSetSearchFragment()
            findNavController().navigate(action)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pagingDataFlow.collect {
                adapter.submitData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect {
                if (it.source.refresh is LoadState.NotLoading) {
                    if (adapter.itemCount > 0)
                        bind.textView.visibility = View.GONE
                    else {
                        bind.textView.visibility = View.VISIBLE
                    }
                }
            }
        }
        bind.searchView.addTextChangedListener(
            { _, _, _, _ -> },
            { text, _, _, _ ->
                val finalText = if (!text.isNullOrBlank()) text.toString().trim() else null
                val oldQuery = activityCallbacks!!.getSearchQuery()
                val newQuery = SearchQuery(
                    oldQuery.countries, oldQuery.genres,
                    oldQuery.order, oldQuery.type,
                    oldQuery.ratingFrom, oldQuery.ratingTo,
                    oldQuery.yearFrom, oldQuery.yearTo, finalText
                )
                activityCallbacks!!.setSearchQuery(newQuery)
                viewModel.createNewQuery(activityCallbacks!!.getSearchQuery())
            },
            { }
        )
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
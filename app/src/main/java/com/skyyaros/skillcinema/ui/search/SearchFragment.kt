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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SearchFragmentBinding
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.filmography.FilmPreviewTwoAdapter
import com.skyyaros.skillcinema.ui.photography.MyLoadStateAdapterTwo

class SearchFragment: Fragment() {
    private var _bind: SearchFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val viewModel: SearchViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    App.component.getKinopoiskRepository(),
                    activityCallbacks!!.getSearchQuery(),
                    activityCallbacks!!.getSeeHistoryFlow().value
                ) as T
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
        val adapter = FilmPreviewTwoAdapter(requireContext()) { filmPreview ->
            val id = filmPreview.kinopoiskId?:filmPreview.filmId!!
            val action = if (activityCallbacks!!.getSearchQuery().nameActor == null) {
                val filmTable = FilmActorTable(
                    "1", id, false, filmPreview.imageUrl,
                    filmPreview.nameRu, filmPreview.nameEn, filmPreview.nameOriginal,
                    filmPreview.genres, filmPreview.rating ?: filmPreview.ratingKinopoisk
                )
                activityCallbacks!!.insertHistoryItem(filmTable)
                SearchFragmentDirections.actionSearchFragmentToDetailFilmFragment(id, "Search")
            }
            else {
                val actorTable = FilmActorTable(
                    "1", id, true, filmPreview.imageUrl,
                    filmPreview.nameRu, filmPreview.nameEn, null, null, null
                )
                activityCallbacks!!.insertHistoryItem(actorTable)
                SearchFragmentDirections.actionSearchFragmentToActorDetailFragment(id, "Search")
            }
            val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
            if (animActive) {
                findNavController().navigate(
                    action,
                    NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(android.R.anim.slide_in_left)
                        .setPopExitAnim(android.R.anim.slide_out_right)
                        .build()
                )
            } else {
                findNavController().navigate(action)
            }
        }
        bind.recyclerView.adapter = adapter.withLoadStateFooter(
            MyLoadStateAdapterTwo {
                adapter.retry()
            }
        )
        if (bind.recyclerView.itemDecorationCount == 0) {
            bind.recyclerView.addItemDecoration(itemMargin)
        }
        if (bind.searchView.text.isNullOrEmpty()) {
            if (activityCallbacks!!.getSearchQuery().keyword != null)
                bind.searchView.setText(activityCallbacks!!.getSearchQuery().keyword!!)
            else if (activityCallbacks!!.getSearchQuery().nameActor != null)
                bind.searchView.setText(activityCallbacks!!.getSearchQuery().nameActor!!)
        }
        bind.searchSettings.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSetSearchFragment()
            val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
            if (animActive) {
                findNavController().navigate(
                    action,
                    NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(android.R.anim.slide_in_left)
                        .setPopExitAnim(android.R.anim.slide_out_right)
                        .build()
                )
            } else {
                findNavController().navigate(action)
            }
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
                        val temp = activityCallbacks!!.getSearchQuery().nameActor
                        if (temp != null && temp.isBlank())
                            bind.textView.text = getString(R.string.search_text_actors)
                        else
                            bind.textView.text = getString(R.string.search_text_no_result)
                        bind.textView.visibility = View.VISIBLE
                    }
                }
                if (it.source.refresh is LoadState.Loading) {
                    bind.progressBar.visibility = View.VISIBLE
                } else {
                    bind.progressBar.visibility = View.GONE
                }
            }
        }
        bind.searchView.addTextChangedListener(
            { _, _, _, _ -> },
            { text, _, _, _ ->
                val oldQuery = activityCallbacks!!.getSearchQuery()
                val isFilmSearch = oldQuery.nameActor == null
                val finalText = if (isFilmSearch) {
                    if (!text.isNullOrBlank()) text.toString().trim() else null
                } else {
                    text?.toString()?.trim() ?: ""
                }
                val newQuery = SearchQuery(
                    oldQuery.countries, oldQuery.genres,
                    oldQuery.order, oldQuery.type,
                    oldQuery.ratingFrom, oldQuery.ratingTo,
                    oldQuery.yearFrom, oldQuery.yearTo,
                    if (isFilmSearch) finalText else oldQuery.keyword,
                    if (!isFilmSearch) finalText else oldQuery.nameActor,
                    oldQuery.showViewedFilms
                )
                activityCallbacks!!.setSearchQuery(newQuery)
                viewModel.createNewQuery(
                    activityCallbacks!!.getSearchQuery(),
                    activityCallbacks!!.getSeeHistoryFlow().value
                )
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
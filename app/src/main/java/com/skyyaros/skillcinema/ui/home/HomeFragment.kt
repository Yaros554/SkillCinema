package com.skyyaros.skillcinema.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.HomeFragmentBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.LeftSpaceDecorator

class HomeFragment: Fragment() {
    private var _bind: HomeFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val viewModel: HomeViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    (requireContext().applicationContext as App).storeRepository,
                    (requireContext().applicationContext as App).kinopoiskRepository
                ) as T
            }
        }
    }
    private var _itemMargin: AdaptiveSpacingItemDecoration? = null
    private val itemMargin get() = _itemMargin!!
    private var _leftMargin: LeftSpaceDecorator? = null
    private val leftMargin get() = _leftMargin!!
    private val onClickFilm: (Long) -> Unit = {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFilmFragment(it)
        findNavController().navigate(action)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
        _itemMargin = AdaptiveSpacingItemDecoration(context.resources.getDimension(R.dimen.small_margin).toInt(), false)
        _leftMargin = LeftSpaceDecorator(context.resources.getDimension(R.dimen.big_margin).toInt())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = HomeFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.hideUpBar()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.statusStartFlow.collect { statusStart ->
                if (statusStart) {
                    activityCallbacks!!.hideDownBar()
                    viewModel.setStartStatus(false)
                    val action = HomeFragmentDirections.actionHomeFragmentToHelloFragment()
                    findNavController().navigate(action)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.filmsFlow.collect { stateHomeFilms ->
                when (stateHomeFilms) {
                    is StateHomeFilms.Loading -> {
                        activityCallbacks!!.hideDownBar()
                        bind.imageView.visibility = View.VISIBLE
                        bind.imageView2.visibility = View.GONE
                        bind.progressBar.visibility = View.VISIBLE
                        bind.buttonReload.visibility = View.GONE
                        bind.textError.visibility = View.GONE
                        bind.scrollView.visibility = View.GONE
                    }
                    is StateHomeFilms.Success -> {
                        activityCallbacks!!.showDownBar()
                        bind.imageView.visibility = View.GONE
                        bind.imageView2.visibility = View.GONE
                        bind.progressBar.visibility = View.GONE
                        bind.buttonReload.visibility = View.GONE
                        bind.textError.visibility = View.GONE
                        bind.scrollView.visibility = View.VISIBLE
                        bind.swipe.isRefreshing = false

                        setup(
                            stateHomeFilms.data.films[0],
                            bind.recyclerPremiers,
                            bind.textPremiers,
                            bind.textPremiersAll,
                            1,
                            -1, null,
                            -1, null,
                            null
                        )
                        setup(
                            stateHomeFilms.data.films[1],
                            bind.recyclerPopulars,
                            bind.textPopulars,
                            bind.textPopularsAll,
                            2,
                            -1, null,
                            -1, null,
                            null
                        )
                        setup(
                            stateHomeFilms.data.films[4],
                            bind.recyclerDynamic1,
                            bind.textDynamic1,
                            bind.textDynamic1All,
                            3,
                            stateHomeFilms.data.countryId1, stateHomeFilms.data.countryName1,
                            stateHomeFilms.data.genreId1, stateHomeFilms.data.genreName1,
                            bind.textviewDynamic1
                        )
                        setup(
                            stateHomeFilms.data.films[2],
                            bind.recyclerTop,
                            bind.textTop,
                            bind.textTopAll,
                            4,
                            -1, null,
                            -1, null,
                            null
                        )
                        setup(
                            stateHomeFilms.data.films[5],
                            bind.recyclerDynamic2,
                            bind.textDynamic2,
                            bind.textDynamic2All,
                            3,
                            stateHomeFilms.data.countryId2, stateHomeFilms.data.countryName2,
                            stateHomeFilms.data.genreId2, stateHomeFilms.data.genreName2,
                            bind.textviewDynamic2
                        )
                        setup(
                            stateHomeFilms.data.films[3],
                            bind.recyclerSerials,
                            bind.textSerials,
                            bind.textSerialsAll,
                            6,
                            -1, null,
                            -1, null,
                            null
                        )
                    }
                    is StateHomeFilms.Error -> {
                        activityCallbacks!!.hideDownBar()
                        bind.imageView.visibility = View.GONE
                        bind.imageView2.visibility = View.VISIBLE
                        bind.progressBar.visibility = View.GONE
                        bind.buttonReload.visibility = View.VISIBLE
                        bind.textError.visibility = View.VISIBLE
                        bind.scrollView.visibility = View.GONE
                        bind.swipe.isRefreshing = false
                    }
                }
            }
        }
        bind.buttonReload.setOnClickListener {
            viewModel.updateFilms()
        }
        bind.swipe.setOnRefreshListener {
            viewModel.updateFilms()
        }
    }

    private fun setup(
        item: List<FilmPreview>?,
        recyclerView: RecyclerView,
        frameLayout: FrameLayout,
        textView: TextView,
        mode: Int,
        countryId: Long, countryName: String?,
        genreId: Long, genreName: String?,
        textViewDynamic: TextView?
    ) {
        if (item != null) {
            frameLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            val onClickList = {
                val action = HomeFragmentDirections.actionHomeFragmentToListpageFragment(
                    item.toTypedArray(), mode,
                    countryId, countryName,
                    genreId, genreName,
                    null
                )
                findNavController().navigate(action)
            }
            val trimList = if (item.size > 20) item.subList(0, 20) else item
            val adapter = FilmPreviewAdapter(trimList, requireContext(), onClickFilm)
            if (item.size > 19) {
                val allAdapter = FilmPreviewAllAdapter { onClickList() }
                recyclerView.adapter = ConcatAdapter(adapter, allAdapter)
            } else {
                recyclerView.adapter = adapter
            }
            if (recyclerView.itemDecorationCount == 0) {
                recyclerView.addItemDecoration(itemMargin)
                recyclerView.addItemDecoration(leftMargin)
            }
            if (textViewDynamic != null)
                textViewDynamic.text = "$genreName, $countryName"
            textView.setOnClickListener { onClickList() }
        } else {
            frameLayout.visibility = View.GONE
            recyclerView.visibility = View.GONE
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
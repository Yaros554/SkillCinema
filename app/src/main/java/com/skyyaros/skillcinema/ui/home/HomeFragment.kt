package com.skyyaros.skillcinema.ui.home

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
import androidx.recyclerview.widget.ConcatAdapter
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.HomeFragmentBinding
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
                return HomeViewModel(App.component.getStoreRepository(), App.component.getKinopoiskRepository()) as T
            }
        }
    }
    private lateinit var premiersAdapter: FilmPreviewAdapter
    private lateinit var popularsAdapter: FilmPreviewAdapter
    private lateinit var dynamic1Adapter: FilmPreviewAdapter
    private lateinit var topAdapter: FilmPreviewAdapter
    private lateinit var dynamic2Adapter: FilmPreviewAdapter
    private lateinit var serialsAdapter: FilmPreviewAdapter
    private lateinit var premiersAllAdapter: FilmPreviewAllAdapter
    private lateinit var popularsAllAdapter: FilmPreviewAllAdapter
    private lateinit var dynamic1AllAdapter: FilmPreviewAllAdapter
    private lateinit var topAllAdapter: FilmPreviewAllAdapter
    private lateinit var dynamic2AllAdapter: FilmPreviewAllAdapter
    private lateinit var serialsAllAdapter: FilmPreviewAllAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
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
                        val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.small_margin).toInt(), false)
                        val leftMargin = LeftSpaceDecorator(requireContext().resources.getDimension(R.dimen.big_margin).toInt())
                        val onClickFilm: (Long) -> Unit = {
                            val action = HomeFragmentDirections.actionHomeFragmentToDetailFilmFragment(it)
                            findNavController().navigate(action)
                        }
                        if (stateHomeFilms.data.films[0] != null) {
                            val onClickList = {
                                val action = HomeFragmentDirections.actionHomeFragmentToListpageFragment(
                                    stateHomeFilms.data.films[0]!!.toTypedArray(),
                                    1,
                                    -1, null,
                                    -1, null
                                )
                                findNavController().navigate(action)
                            }
                            val trimList = if (stateHomeFilms.data.films[0]!!.size > 20)
                                stateHomeFilms.data.films[0]!!.subList(0, 20)
                            else
                                stateHomeFilms.data.films[0]!!
                            premiersAdapter = FilmPreviewAdapter(trimList, requireContext(), onClickFilm)
                            bind.textPremiers.visibility = View.VISIBLE
                            bind.recyclerPremiers.visibility = View.VISIBLE
                            if (stateHomeFilms.data.films[0]!!.size > 20) {
                                premiersAllAdapter = FilmPreviewAllAdapter { onClickList() }
                                bind.recyclerPremiers.adapter = ConcatAdapter(premiersAdapter, premiersAllAdapter)
                            } else {
                                bind.recyclerPremiers.adapter = premiersAdapter
                            }
                            if (bind.recyclerPremiers.itemDecorationCount == 0) {
                                bind.recyclerPremiers.addItemDecoration(itemMargin)
                                bind.recyclerPremiers.addItemDecoration(leftMargin)
                            }
                            bind.textPremiersAll.setOnClickListener { onClickList() }
                        } else {
                            bind.textPremiers.visibility = View.GONE
                            bind.recyclerPremiers.visibility = View.GONE
                        }
                        if (stateHomeFilms.data.films[1] != null) {
                            val onClick = {
                                val action = HomeFragmentDirections.actionHomeFragmentToListpageFragment(
                                    stateHomeFilms.data.films[1]!!.toTypedArray(),
                                    2,
                                    -1, null,
                                    -1, null
                                )
                                findNavController().navigate(action)
                            }
                            popularsAdapter = FilmPreviewAdapter(stateHomeFilms.data.films[1]!!.shuffled(), requireContext(), onClickFilm)
                            bind.textPopulars.visibility = View.VISIBLE
                            bind.recyclerPopulars.visibility = View.VISIBLE
                            if (stateHomeFilms.data.films[1]!!.size >= 20) {
                                popularsAllAdapter = FilmPreviewAllAdapter { onClick() }
                                bind.recyclerPopulars.adapter = ConcatAdapter(popularsAdapter, popularsAllAdapter)
                            } else {
                                bind.recyclerPopulars.adapter = popularsAdapter
                            }
                            if (bind.recyclerPopulars.itemDecorationCount == 0) {
                                bind.recyclerPopulars.addItemDecoration(itemMargin)
                                bind.recyclerPopulars.addItemDecoration(leftMargin)
                            }
                            bind.textPopularsAll.setOnClickListener { onClick() }
                        } else {
                            bind.textPopulars.visibility = View.GONE
                            bind.recyclerPopulars.visibility = View.GONE
                        }
                        if (stateHomeFilms.data.films[4] != null) {
                            val onClick = {
                                val action = HomeFragmentDirections.actionHomeFragmentToListpageFragment(
                                    stateHomeFilms.data.films[4]!!.toTypedArray(),
                                    3,
                                    stateHomeFilms.data.countryId1, stateHomeFilms.data.countryName1,
                                    stateHomeFilms.data.genreId1, stateHomeFilms.data.genreName1
                                )
                                findNavController().navigate(action)
                            }
                            dynamic1Adapter = FilmPreviewAdapter(stateHomeFilms.data.films[4]!!.shuffled(), requireContext(), onClickFilm)
                            bind.textDynamic1.visibility = View.VISIBLE
                            bind.textviewDynamic1.text = "${stateHomeFilms.data.genreName1}, ${stateHomeFilms.data.countryName1}"
                            bind.recyclerDynamic1.visibility = View.VISIBLE
                            if (stateHomeFilms.data.films[4]!!.size >= 20) {
                                dynamic1AllAdapter = FilmPreviewAllAdapter { onClick() }
                                bind.recyclerDynamic1.adapter = ConcatAdapter(dynamic1Adapter, dynamic1AllAdapter)
                            } else {
                                bind.recyclerDynamic1.adapter = dynamic1Adapter
                            }
                            if (bind.recyclerDynamic1.itemDecorationCount == 0) {
                                bind.recyclerDynamic1.addItemDecoration(itemMargin)
                                bind.recyclerDynamic1.addItemDecoration(leftMargin)
                            }
                            bind.textDynamic1All.setOnClickListener { onClick() }
                        } else {
                            bind.textDynamic1.visibility = View.GONE
                            bind.recyclerDynamic1.visibility = View.GONE
                        }
                        if (stateHomeFilms.data.films[2] != null) {
                            val onClick = {
                                val action = HomeFragmentDirections.actionHomeFragmentToListpageFragment(
                                    stateHomeFilms.data.films[2]!!.toTypedArray(),
                                    4,
                                    -1, null,
                                    -1, null
                                )
                                findNavController().navigate(action)
                            }
                            topAdapter = FilmPreviewAdapter(stateHomeFilms.data.films[2]!!.shuffled(), requireContext(), onClickFilm)
                            bind.textTop.visibility = View.VISIBLE
                            bind.recyclerTop.visibility = View.VISIBLE
                            if (stateHomeFilms.data.films[2]!!.size >= 20) {
                                topAllAdapter = FilmPreviewAllAdapter { onClick() }
                                bind.recyclerTop.adapter = ConcatAdapter(topAdapter, topAllAdapter)
                            } else {
                                bind.recyclerTop.adapter = topAdapter
                            }
                            if (bind.recyclerTop.itemDecorationCount == 0) {
                                bind.recyclerTop.addItemDecoration(itemMargin)
                                bind.recyclerTop.addItemDecoration(leftMargin)
                            }
                            bind.textTopAll.setOnClickListener { onClick() }
                        } else {
                            bind.textTop.visibility = View.GONE
                            bind.recyclerTop.visibility = View.GONE
                        }
                        if (stateHomeFilms.data.films[5] != null) {
                            val onClick = {
                                val action = HomeFragmentDirections.actionHomeFragmentToListpageFragment(
                                    stateHomeFilms.data.films[5]!!.toTypedArray(),
                                    3,
                                    stateHomeFilms.data.countryId2, stateHomeFilms.data.countryName2,
                                    stateHomeFilms.data.genreId2, stateHomeFilms.data.genreName2
                                )
                                findNavController().navigate(action)
                            }
                            dynamic2Adapter = FilmPreviewAdapter(stateHomeFilms.data.films[5]!!.shuffled(), requireContext(), onClickFilm)
                            bind.textDynamic2.visibility = View.VISIBLE
                            bind.textviewDynamic2.text = "${stateHomeFilms.data.genreName2}, ${stateHomeFilms.data.countryName2}"
                            bind.recyclerDynamic2.visibility = View.VISIBLE
                            if (stateHomeFilms.data.films[5]!!.size >= 20) {
                                dynamic2AllAdapter = FilmPreviewAllAdapter { onClick() }
                                bind.recyclerDynamic2.adapter = ConcatAdapter(dynamic2Adapter, dynamic2AllAdapter)
                            } else {
                                bind.recyclerDynamic2.adapter = dynamic2Adapter
                            }
                            if (bind.recyclerDynamic2.itemDecorationCount == 0) {
                                bind.recyclerDynamic2.addItemDecoration(itemMargin)
                                bind.recyclerDynamic2.addItemDecoration(leftMargin)
                            }
                            bind.textDynamic2All.setOnClickListener { onClick() }
                        } else {
                            bind.textDynamic2.visibility = View.GONE
                            bind.recyclerDynamic2.visibility = View.GONE
                        }
                        if (stateHomeFilms.data.films[3] != null) {
                            val onClick = {
                                val action = HomeFragmentDirections.actionHomeFragmentToListpageFragment(
                                    stateHomeFilms.data.films[3]!!.toTypedArray(),
                                    6,
                                    -1, null,
                                    -1, null
                                )
                                findNavController().navigate(action)
                            }
                            serialsAdapter = FilmPreviewAdapter(stateHomeFilms.data.films[3]!!.shuffled(), requireContext(), onClickFilm)
                            bind.textSerials.visibility = View.VISIBLE
                            bind.recyclerSerials.visibility = View.VISIBLE
                            if (stateHomeFilms.data.films[3]!!.size >= 20) {
                                serialsAllAdapter = FilmPreviewAllAdapter(onClick)
                                bind.recyclerSerials.adapter = ConcatAdapter(serialsAdapter, serialsAllAdapter)
                            } else {
                                bind.recyclerSerials.adapter = serialsAdapter
                            }
                            if (bind.recyclerSerials.itemDecorationCount == 0) {
                                bind.recyclerSerials.addItemDecoration(itemMargin)
                                bind.recyclerSerials.addItemDecoration(leftMargin)
                            }
                            bind.textSerialsAll.setOnClickListener { onClick() }
                        } else {
                            bind.textSerials.visibility = View.GONE
                            bind.recyclerSerials.visibility = View.GONE
                        }
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

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}
package com.skyyaros.skillcinema.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SetSearchFragmentBinding
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.BackDialogResult
import com.skyyaros.skillcinema.ui.BackDialogViewModel
import com.skyyaros.skillcinema.ui.BackPressedListener
import com.skyyaros.skillcinema.ui.SearchSettingsViewModel
import com.skyyaros.skillcinema.ui.dopsearch.SearchGenreCountryMode
import com.skyyaros.skillcinema.ui.dopsearch.TypeSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

class SetSearchFragment: Fragment(), BackPressedListener {
    private var activityCallbacks: ActivityCallbacks? = null
    private var _bind: SetSearchFragmentBinding? = null
    private val bind get() = _bind!!
    private lateinit var oldQuery: SearchQuery
    private var fragmentWillKill = false
    private val viewModel: SetSearchViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SetSearchViewModel(oldQuery, App.component.getKinopoiskRepository()) as T
            }
        }
    }
    private val backDialogViewModel: BackDialogViewModel by activityViewModels()
    private val searchSettingsViewModel: SearchSettingsViewModel by activityViewModels()

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
        bind.switchMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideShowUi(View.GONE)
                viewModel.nameActor = ""
            } else {
                hideShowUi(View.VISIBLE)
                viewModel.nameActor = null
            }
        }
        bind.rangeSlider.addOnChangeListener { _, _, _ ->
            if (bind.rangeSlider.values.min() == 0f && bind.rangeSlider.values.max() == 10f)
                bind.rating.text = getString(R.string.search_set_text_rating_value1)
            else
                bind.rating.text = getString(
                    R.string.search_set_text_rating_value2,
                    bind.rangeSlider.values.min().toInt(),
                    bind.rangeSlider.values.max().toInt()
                )
            viewModel.ratingFrom = bind.rangeSlider.values.min().toInt()
            viewModel.ratingTo = bind.rangeSlider.values.max().toInt()
        }
        bind.chipAll.setOnClickListener {
            bind.chipAll.isChecked = true
            bind.chipFilms.isChecked = false
            bind.chipSerials.isChecked = false
            viewModel.type = "ALL"
        }
        bind.chipFilms.setOnClickListener {
            bind.chipAll.isChecked = false
            bind.chipFilms.isChecked = true
            bind.chipSerials.isChecked = false
            viewModel.type = "FILM"
        }
        bind.chipSerials.setOnClickListener {
            bind.chipAll.isChecked = false
            bind.chipFilms.isChecked = false
            bind.chipSerials.isChecked = true
            viewModel.type = "TV_SERIES"
        }
        bind.chipDate.setOnClickListener {
            bind.chipDate.isChecked = true
            bind.chipPopularity.isChecked = false
            bind.chipRating.isChecked = false
            viewModel.order = "YEAR"
        }
        bind.chipPopularity.setOnClickListener {
            bind.chipDate.isChecked = false
            bind.chipPopularity.isChecked = true
            bind.chipRating.isChecked = false
            viewModel.order = "NUM_VOTE"
        }
        bind.chipRating.setOnClickListener {
            bind.chipDate.isChecked = false
            bind.chipPopularity.isChecked = false
            bind.chipRating.isChecked = true
            viewModel.order = "RATING"
        }
        bind.frameCountry.setOnClickListener {
            if (viewModel.countries != null) {
                val action = SetSearchFragmentDirections.actionSetSearchFragmentToSearchGenreCountryFragment(
                    SearchGenreCountryMode.COUNTRY.ordinal,
                    viewModel.country?:-1L
                )
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
            } else {
                Toast.makeText(requireContext(), getString(R.string.search_set_toast), Toast.LENGTH_LONG).show()
            }
        }
        bind.frameGenre.setOnClickListener {
            if (viewModel.genres != null) {
                val action = SetSearchFragmentDirections.actionSetSearchFragmentToSearchGenreCountryFragment(
                    SearchGenreCountryMode.GENRE.ordinal,
                    viewModel.genre?:-1L
                )
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
            } else {
                Toast.makeText(requireContext(), getString(R.string.search_set_toast), Toast.LENGTH_LONG).show()
            }
        }
        bind.frameYear.setOnClickListener {
            val action = SetSearchFragmentDirections.actionSetSearchFragmentToSearchYearFragment(viewModel.yearFrom, viewModel.yearTo)
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
        bind.textSee.setOnClickListener {
            viewModel.showViewedFilms = !viewModel.showViewedFilms
            if (viewModel.showViewedFilms) {
                bind.textSee.text = getString(R.string.search_set_text_show_viewed_films)
                bind.imageSee.setImageDrawable(resources.getDrawable(R.drawable.see_black_white))
            } else {
                bind.textSee.text = getString(R.string.search_set_text_hide_viewed_films)
                bind.imageSee.setImageDrawable(resources.getDrawable(R.drawable.unsee_black_white))
            }
        }
        bind.button.setOnClickListener {
            val newQuery = if (viewModel.nameActor == null) {
                SearchQuery(
                    viewModel.country, viewModel.genre,
                    viewModel.order, viewModel.type,
                    viewModel.ratingFrom, viewModel.ratingTo,
                    viewModel.yearFrom, viewModel.yearTo,
                    oldQuery.keyword, viewModel.nameActor, viewModel.showViewedFilms
                )
            } else {
                if (oldQuery.nameActor == null)
                    SearchQuery(nameActor = viewModel.nameActor)
                else
                    SearchQuery(nameActor = oldQuery.nameActor)
            }
            if (oldQuery != newQuery) {
                activityCallbacks!!.setSearchQuery(newQuery)
                val action = SetSearchFragmentDirections.actionSetSearchFragmentToSearchFragment()
                val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
                if (animActive) {
                    findNavController().navigate(
                        action,
                        NavOptions.Builder()
                            .setEnterAnim(android.R.anim.slide_in_left)
                            .setExitAnim(android.R.anim.slide_out_right)
                            .build()
                    )
                } else {
                    findNavController().navigate(action)
                }
            } else {
                fragmentWillKill = true
                backPressedListener = null
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initCurrentSet()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            backDialogViewModel.resBackDialog.collect {
                backDialogViewModel.cleanResBackDialog()
                if (it == BackDialogResult.OK) {
                    val newQuery = if (viewModel.nameActor == null) {
                        SearchQuery(
                            viewModel.country, viewModel.genre,
                            viewModel.order, viewModel.type,
                            viewModel.ratingFrom, viewModel.ratingTo,
                            viewModel.yearFrom, viewModel.yearTo,
                            oldQuery.keyword, viewModel.nameActor, viewModel.showViewedFilms
                        )
                    } else {
                        if (oldQuery.nameActor == null)
                            SearchQuery(nameActor = viewModel.nameActor)
                        else
                            SearchQuery(nameActor = oldQuery.nameActor)
                    }
                    activityCallbacks!!.setSearchQuery(newQuery)
                    val action = SetSearchFragmentDirections.actionSetSearchFragmentToSearchFragment()
                    while ((findNavController().currentDestination?.label ?: "") != "SetSearchFragment")
                        delay(1)
                    val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
                    if (animActive) {
                        findNavController().navigate(
                            action,
                            NavOptions.Builder()
                                .setEnterAnim(android.R.anim.slide_in_left)
                                .setExitAnim(android.R.anim.slide_out_right)
                                .build()
                        )
                    } else {
                        findNavController().navigate(action)
                    }
                } else if (it == BackDialogResult.NO) {
                    fragmentWillKill = true
                    backPressedListener = null
                    requireActivity().onBackPressed()
                } else {
                    delay(50) //костыль, чтобы не сбивался toolbar
                    activityCallbacks!!.showUpBar(getString(R.string.search_set_title))
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchSettingsViewModel.SearchSettingsFlow.collect {
                searchSettingsViewModel.cleanSearchSettings()
                when (it.type) {
                    TypeSettings.COUNTRY -> {
                        if (it.countryId == -1L) {
                            viewModel.country = null
                            bind.country.text = getString(R.string.search_set_text_all)
                        } else {
                            viewModel.country = it.countryId
                            bind.country.text = viewModel.countries!!.find { country -> country.id == it.countryId }!!.country
                        }
                    }
                    TypeSettings.GENRE -> {
                        if (it.genreId == -1L) {
                            viewModel.genre = null
                            bind.genre.text = getString(R.string.search_set_text_all)
                        } else {
                            viewModel.genre = it.genreId
                            bind.genre.text = viewModel.genres!!.find { genre-> genre.id == it.genreId }!!.genre
                        }
                    }
                    else -> {
                        viewModel.yearFrom = it.yearFrom
                        viewModel.yearTo = it.yearTo
                        if (viewModel.yearFrom == 1000 && viewModel.yearTo == 3000)
                            bind.year.text = getString(R.string.search_set_text_rating_value1)
                        else
                            bind.year.text = getString(R.string.search_set_text_year_value, viewModel.yearFrom, viewModel.yearTo)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityCallbacks!!.showUpBar(getString(R.string.search_set_title))
        if (!fragmentWillKill)
            backPressedListener = this
    }

    private fun initCurrentSet() {
        if (viewModel.type == "ALL") {
            bind.chipAll.isChecked = true
            bind.chipFilms.isChecked = false
            bind.chipSerials.isChecked = false
        } else if (viewModel.type == "FILM") {
            bind.chipAll.isChecked = false
            bind.chipFilms.isChecked = true
            bind.chipSerials.isChecked = false
        } else {
            bind.chipAll.isChecked = false
            bind.chipFilms.isChecked = false
            bind.chipSerials.isChecked = true
        }
        if (viewModel.country == null)
            bind.country.text = getString(R.string.search_set_text_all)
        else {
            val nameCountry = viewModel.countries!!.find { it.id == viewModel.country }?.country
            if (nameCountry != null) {
                bind.country.text = nameCountry
            } else {
                bind.country.text = getString(R.string.search_set_text_all)
                viewModel.country = null
                activityCallbacks!!.setSearchQuery(
                    SearchQuery(
                        null, oldQuery.genres,
                        oldQuery.order, oldQuery.type,
                        oldQuery.ratingFrom, oldQuery.ratingTo,
                        oldQuery.yearFrom, oldQuery.yearTo,
                        oldQuery.keyword, oldQuery.nameActor, oldQuery.showViewedFilms
                    )
                )
                oldQuery = activityCallbacks!!.getSearchQuery()
            }
        }
        if (viewModel.genre == null)
            bind.genre.text = getString(R.string.search_set_text_all)
        else {
            val nameGenre = viewModel.genres!!.find { it.id == viewModel.genre }?.genre
            if (nameGenre != null) {
                bind.genre.text = nameGenre
            } else {
                bind.genre.text = getString(R.string.search_set_text_all)
                viewModel.genre = null
                activityCallbacks!!.setSearchQuery(
                    SearchQuery(
                        oldQuery.countries, null,
                        oldQuery.order, oldQuery.type,
                        oldQuery.ratingFrom, oldQuery.ratingTo,
                        oldQuery.yearFrom, oldQuery.yearTo,
                        oldQuery.keyword, oldQuery.nameActor, oldQuery.showViewedFilms
                    )
                )
                oldQuery = activityCallbacks!!.getSearchQuery()
            }
        }
        if (viewModel.yearFrom == 1000 && viewModel.yearTo == 3000)
            bind.year.text = getString(R.string.search_set_text_rating_value1)
        else
            bind.year.text = getString(R.string.search_set_text_year_value, viewModel.yearFrom, viewModel.yearTo)
        bind.rangeSlider.setValues(viewModel.ratingFrom.toFloat(), viewModel.ratingTo.toFloat())
        if (bind.rangeSlider.values.min() == 0f && bind.rangeSlider.values.max() == 10f)
            bind.rating.text = getString(R.string.search_set_text_rating_value1)
        else
            bind.rating.text = getString(
                R.string.search_set_text_rating_value2,
                bind.rangeSlider.values.min().toInt(),
                bind.rangeSlider.values.max().toInt()
            )
        if (viewModel.order == "YEAR") {
            bind.chipDate.isChecked = true
            bind.chipPopularity.isChecked = false
            bind.chipRating.isChecked = false
        } else if (viewModel.order == "NUM_VOTE") {
            bind.chipDate.isChecked = false
            bind.chipPopularity.isChecked = true
            bind.chipRating.isChecked = false
        } else {
            bind.chipDate.isChecked = false
            bind.chipPopularity.isChecked = false
            bind.chipRating.isChecked = true
        }
        if (viewModel.showViewedFilms) {
            bind.textSee.text = getString(R.string.search_set_text_show_viewed_films)
            bind.imageSee.setImageDrawable(resources.getDrawable(R.drawable.see_black_white))
        } else {
            bind.textSee.text = getString(R.string.search_set_text_hide_viewed_films)
            bind.imageSee.setImageDrawable(resources.getDrawable(R.drawable.unsee_black_white))
        }
        if (viewModel.nameActor == null) {
            hideShowUi(View.VISIBLE)
            bind.switchMode.isChecked = false
        } else {
            hideShowUi(View.GONE)
            bind.switchMode.isChecked = true
        }
    }

    private fun hideShowUi(hideShow: Int) {
        bind.show.visibility = hideShow
        bind.chipAll.visibility = hideShow
        bind.chipSerials.visibility = hideShow
        bind.chipFilms.visibility = hideShow
        bind.linearLayout.visibility = hideShow
        bind.sort.visibility = hideShow
        bind.chipDate.visibility = hideShow
        bind.chipRating.visibility = hideShow
        bind.chipPopularity.visibility = hideShow
        bind.divider.visibility = hideShow
        bind.imageSee.visibility = hideShow
        bind.textSee.visibility = hideShow
        bind.animation.visibility = if (hideShow == View.VISIBLE)
            View.GONE
        else
            View.VISIBLE
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
        val newQuery = if (viewModel.nameActor == null) {
            SearchQuery(
                viewModel.country, viewModel.genre,
                viewModel.order, viewModel.type,
                viewModel.ratingFrom, viewModel.ratingTo,
                viewModel.yearFrom, viewModel.yearTo,
                oldQuery.keyword, viewModel.nameActor, viewModel.showViewedFilms
            )
        } else {
            if (oldQuery.nameActor == null)
                SearchQuery(nameActor = viewModel.nameActor)
            else
                SearchQuery(nameActor = oldQuery.nameActor)
        }
        return if (oldQuery == newQuery)
            false
        else {
            val action = SetSearchFragmentDirections.actionSetSearchFragmentToBackDialog()
            findNavController().navigate(action)
            true
        }
    }

    companion object {
        var backPressedListener: BackPressedListener? = null
    }
}
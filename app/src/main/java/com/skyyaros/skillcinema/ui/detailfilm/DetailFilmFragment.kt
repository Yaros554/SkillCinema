package com.skyyaros.skillcinema.ui.detailfilm

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.Configuration
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.DetailFilmFragmentBinding
import com.skyyaros.skillcinema.entity.ActorPreview
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.LeftSpaceDecorator
import com.skyyaros.skillcinema.ui.home.FilmPreviewAdapter
import com.skyyaros.skillcinema.ui.home.FilmPreviewAllAdapter
import java.util.*

class DetailFilmFragment: Fragment() {
    private var _bind: DetailFilmFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: DetailFilmFragmentArgs by navArgs()
    private val viewModel: DetailFilmViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailFilmViewModel(App.component.getKinopoiskRepository(), args.id) as T
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = DetailFilmFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.showUpBar("")
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.detailFilmFlow.collect { stateDetailFilm ->
                when (stateDetailFilm) {
                    is StateDetailFilm.Loading -> {
                        bind.scrollView.visibility = View.GONE
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            hideOrShowImage(View.GONE)
                        bind.errorLayout.visibility = View.GONE
                        bind.buttonReload.visibility = View.GONE
                        bind.progressBar.visibility = View.VISIBLE
                    }
                    is StateDetailFilm.Success -> {
                        bind.scrollView.visibility = View.VISIBLE
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            hideOrShowImage(View.VISIBLE)
                        bind.errorLayout.visibility = View.GONE
                        bind.buttonReload.visibility = View.GONE
                        bind.progressBar.visibility = View.GONE

                        val item = stateDetailFilm.data.detailFilm
                        val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                            resources.getDrawable(R.drawable.empty_night)
                        } else {
                            resources.getDrawable(R.drawable.empty)
                        }
                        if (item.coverUrl != null) {
                            Glide.with(bind.poster.context).load(item.coverUrl).placeholder(placeholderId).into(bind.poster)
                            if (item.logoUrl != null) {
                                bind.bigName.visibility = View.GONE
                                bind.logo.visibility = View.VISIBLE
                                Glide.with(bind.logo.context).load(item.logoUrl).into(bind.logo)
                            } else {
                                bind.bigName.visibility = View.VISIBLE
                                bind.logo.visibility = View.GONE
                                val bigName = if (Locale.getDefault().language == "ru") {
                                    if (!item.nameRu.isNullOrBlank())
                                        item.nameRu
                                    else if (!item.nameEn.isNullOrBlank())
                                        item.nameEn
                                    else if (!item.nameOriginal.isNullOrBlank())
                                        item.nameOriginal
                                    else
                                        getString(R.string.home_text_no_name)
                                } else {
                                    if (!item.nameEn.isNullOrBlank())
                                        item.nameEn
                                    else if (!item.nameOriginal.isNullOrBlank())
                                        item.nameOriginal
                                    else if (!item.nameRu.isNullOrBlank())
                                        item.nameRu
                                    else
                                        getString(R.string.home_text_no_name)
                                }
                                bind.bigName.text = bigName
                            }
                        } else {
                            Glide.with(bind.poster.context).load(item.posterUrl).placeholder(placeholderId).into(bind.poster)
                            bind.bigName.visibility = View.GONE
                            bind.logo.visibility = View.GONE
                        }
                        bind.imageLike.setImageResource(R.drawable.unlike)
                        bind.imageFuture.setImageResource(R.drawable.unfuture)
                        bind.imageSee.setImageResource(R.drawable.unsee)
                        if (item.ratingKinopoisk == null && item.nameOriginal == null) {
                            bind.info1.visibility = View.GONE
                        } else {
                            bind.info1.text = "${item.ratingKinopoisk ?: ""} ${item.nameOriginal ?: ""}"
                        }
                        val genres = StringBuilder("")
                        item.genres.forEach {
                            genres.append(it.genre)
                            genres.append(", ")
                        }
                        bind.info2.text = "${item.year ?: ""}, ${genres.removeSuffix(", ")}"
                        val countries = StringBuilder("")
                        item.countries.forEach {
                            countries.append(it.country)
                            countries.append(", ")
                        }
                        val time = if (item.filmLength != null) {
                            getString(R.string.detail_string_time, item.filmLength / 60, item.filmLength % 60)
                        } else {
                            ""
                        }
                        bind.info3.text = "$countries$time, ${item.ratingAgeLimits?.substring(3)?.plus("+") ?: ""}"
                        if (item.shortDescription != null)
                            bind.shortDescription.text = item.shortDescription
                        else
                            bind.shortDescription.visibility = View.GONE
                        if (item.description != null) {
                            if (item.description.length > 250) {
                                val transition = LayoutTransition()
                                transition.setDuration(300)
                                transition.enableTransitionType(LayoutTransition.CHANGING)
                                transition.addTransitionListener(object: LayoutTransition.TransitionListener {
                                    override fun startTransition(transition: LayoutTransition, container: ViewGroup, view: View, transitionType: Int) {
                                        //todo
                                    }

                                    override fun endTransition(transition: LayoutTransition, container: ViewGroup, view: View, transitionType: Int) {
                                        viewModel.animationActive = false
                                    }
                                })
                                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                                    bind.parentLayout!!.layoutTransition = transition
                                else
                                    bind.parentLandLayout!!.layoutTransition = transition

                                bind.description.isClickable = true
                                bind.description.setOnClickListener {
                                    if (viewModel.animationActive) {
                                        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                                            bind.parentLayout!!.layoutTransition = bind.parentLayout!!.layoutTransition
                                        else
                                            bind.parentLandLayout!!.layoutTransition = bind.parentLandLayout!!.layoutTransition
                                    }
                                    viewModel.animationActive = true
                                    if (viewModel.isCollapsing) {
                                        bind.description.text = item.description
                                    } else {
                                        bind.description.text = "${item.description.substring(0, 250)}..."
                                    }
                                    viewModel.isCollapsing = !viewModel.isCollapsing
                                }

                                if (viewModel.isCollapsing) {
                                    bind.description.text = "${item.description.substring(0, 250)}..."
                                } else {
                                    bind.description.text = item.description
                                }
                            } else {
                                bind.description.isClickable = false
                                bind.description.text = item.description
                            }
                        }
                        else
                            bind.description.visibility = View.GONE
                        val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.small_margin).toInt(), false)
                        val leftMargin = LeftSpaceDecorator(requireContext().resources.getDimension(R.dimen.big_margin).toInt())
                        if (stateDetailFilm.data.actors != null) {
                            val onClick: (Long)->Unit = {id ->
                                val action = DetailFilmFragmentDirections.actionDetailFilmFragmentToActorDetailFragment(id)
                                findNavController().navigate(action)
                            }
                            val onClickListActors: (List<ActorPreview>, String)-> Unit = { data, label ->
                                val action = DetailFilmFragmentDirections.actionDetailFilmFragmentToActerListFragment(data.toTypedArray(), label)
                                findNavController().navigate(action)
                            }
                            val listActors = stateDetailFilm.data.actors.filter { it.professionKey == "ACTOR" && (!it.nameRu.isNullOrBlank() || !it.nameEn.isNullOrBlank()) }
                            val listProducers = stateDetailFilm.data.actors.filter { it.professionKey != "ACTOR" && (!it.nameRu.isNullOrBlank() || !it.nameEn.isNullOrBlank()) }
                            if (listActors.isNotEmpty()) {
                                val trimList = if (listActors.size > 20) listActors.subList(0, 20) else listActors
                                val listActorsAdapter = ActerPreviewAdapter(trimList, requireContext(), onClick)
                                bind.listActers.adapter = listActorsAdapter
                                if (bind.listActers.itemDecorationCount == 0) {
                                    bind.listActers.addItemDecoration(itemMargin)
                                    bind.listActers.addItemDecoration(leftMargin)
                                }
                                if (listActors.size > 20) {
                                    bind.textCountActers.text = listActors.size.toString()
                                    bind.textCountActers.setOnClickListener { onClickListActors(listActors, getString(R.string.detail_text_acters)) }
                                }
                                else
                                    bind.textCountActers.visibility = View.GONE
                            } else {
                                bind.listActers.visibility = View.GONE
                                bind.layoutActers.visibility = View.GONE
                            }
                            if (listProducers.isNotEmpty()) {
                                val trimList = if (listProducers.size > 6) listProducers.subList(0, 6) else listProducers
                                val listProducersAdapter = ActerPreviewAdapter(trimList, requireContext(), onClick)
                                bind.listProducers.adapter = listProducersAdapter
                                if (bind.listProducers.itemDecorationCount == 0) {
                                    bind.listProducers.addItemDecoration(itemMargin)
                                    bind.listProducers.addItemDecoration(leftMargin)
                                }
                                if (listProducers.size > 6) {
                                    bind.textCountProducers.text = listProducers.size.toString()
                                    bind.textCountProducers.setOnClickListener { onClickListActors(listProducers, getString(R.string.detail_text_producers)) }
                                }
                                else
                                    bind.textCountProducers.visibility = View.GONE
                            } else {
                                bind.listProducers.visibility = View.GONE
                                bind.layoutProducers.visibility = View.GONE
                            }
                        } else {
                            bind.listActers.visibility = View.GONE
                            bind.layoutActers.visibility = View.GONE
                            bind.listProducers.visibility = View.GONE
                            bind.layoutProducers.visibility = View.GONE
                        }
                        if (stateDetailFilm.data.images != null) {
                            val imageList = mutableListOf<ImageItem>()
                            var count = 0
                            stateDetailFilm.data.images.forEach {
                                imageList += it.items
                                count += it.total
                            }
                            val trimList = if (imageList.size > 20) imageList.subList(0, 20).toList() else imageList.toList()
                            val listPhotoPreviewAdapter = PhotoPreviewAdapter(trimList, requireContext())
                            bind.listPhotos.adapter = listPhotoPreviewAdapter
                            if (bind.listPhotos.itemDecorationCount == 0) {
                                bind.listPhotos.addItemDecoration(itemMargin)
                                bind.listPhotos.addItemDecoration(leftMargin)
                            }
                            if (imageList.size > 20) {
                                bind.textCountPhotos.text = count.toString()
                                bind.textCountPhotos.setOnClickListener {
                                    val action = DetailFilmFragmentDirections.actionDetailFilmFragmentToPhotographyFragment(stateDetailFilm.data.images.toTypedArray(), args.id)
                                    findNavController().navigate(action)
                                }
                            } else {
                                bind.textCountPhotos.visibility = View.GONE
                            }
                        } else {
                            bind.layoutGallery.visibility = View.GONE
                            bind.listPhotos.visibility = View.GONE
                        }
                        if (stateDetailFilm.data.similar != null && stateDetailFilm.data.similar.isNotEmpty()) {
                            val onClick: (Long)->Unit = {
                                val action = DetailFilmFragmentDirections.actionDetailFilmFragmentToDetailFilmFragment(it)
                                findNavController().navigate(action)
                            }
                            val onClickList: ()->Unit = {
                                val action = DetailFilmFragmentDirections.actionDetailFilmFragmentToListpageFragment(
                                    stateDetailFilm.data.similar.toTypedArray(),
                                    7,
                                    -1, null,
                                    -1, null
                                )
                                findNavController().navigate(action)
                            }
                            val trimList = if (stateDetailFilm.data.similar.size > 10) stateDetailFilm.data.similar.subList(0, 10) else stateDetailFilm.data.similar
                            val adapter = FilmPreviewAdapter(trimList, requireContext(), onClick)
                            if (stateDetailFilm.data.similar.size > 10) {
                                val allAdapter = FilmPreviewAllAdapter { onClickList() }
                                bind.listFilms.adapter = ConcatAdapter(adapter, allAdapter)
                            } else {
                                bind.listFilms.adapter = adapter
                            }
                            if (bind.listFilms.itemDecorationCount == 0) {
                                bind.listFilms.addItemDecoration(itemMargin)
                                bind.listFilms.addItemDecoration(leftMargin)
                            }
                            bind.textCountFilms.text = stateDetailFilm.data.similar.size.toString()
                            if (stateDetailFilm.data.similar.size > 10) {
                                bind.textCountFilms.setOnClickListener { onClickList() }
                            }
                        } else {
                            bind.layoutFilms.visibility = View.GONE
                            bind.listFilms.visibility = View.GONE
                        }
                        if (stateDetailFilm.data.series != null) {
                            val countSeasons = stateDetailFilm.data.series.size
                            val countEpisodes = stateDetailFilm.data.series.fold(0) { acc, elem ->
                                acc + elem.episodes.size
                            }
                            if (Locale.getDefault().language == "ru") {
                                val stringSeason = if (countSeasons in 11..19)
                                    "сезонов"
                                else if (countSeasons % 10 == 1)
                                    "сезон"
                                else if (countSeasons % 10 in 2..4)
                                    "сезона"
                                else
                                    "сезонов"
                                val stringEpisodes = if (countEpisodes in 11..19)
                                    "серий"
                                else if (countEpisodes % 10 == 1)
                                    "серия"
                                else if (countEpisodes % 10 in 2..4)
                                    "серии"
                                else
                                    "серий"
                                bind.countSeries.text = "$countSeasons $stringSeason, $countEpisodes $stringEpisodes"
                                bind.info2.text = bind.info2.text.toString() + " $countSeasons $stringSeason"
                            } else {
                                bind.countSeries.text = "$countSeasons seasons, $countEpisodes episodes"
                                bind.info2.text = bind.info2.text.toString() + " $countSeasons seasons"
                            }
                            bind.layoutSeries.setOnClickListener {
                                val elem = stateDetailFilm.data.detailFilm
                                val name = if (Locale.getDefault().language == "ru") {
                                    if (!elem.nameRu.isNullOrBlank())
                                        elem.nameRu
                                    else if (!elem.nameEn.isNullOrBlank())
                                        elem.nameEn
                                    else if (!elem.nameOriginal.isNullOrBlank())
                                        elem.nameOriginal
                                    else
                                        ""
                                } else {
                                    if (!elem.nameEn.isNullOrBlank())
                                        elem.nameEn
                                    else if (!elem.nameOriginal.isNullOrBlank())
                                        elem.nameOriginal
                                    else if (!elem.nameRu.isNullOrBlank())
                                        elem.nameRu
                                    else
                                        ""
                                }
                                val action = DetailFilmFragmentDirections.actionDetailFilmFragmentToSeriesFragment(stateDetailFilm.data.series.toTypedArray(), name)
                                findNavController().navigate(action)
                            }
                        } else {
                            bind.layoutSeries.visibility = View.GONE
                        }
                    }
                    is StateDetailFilm.Error -> {
                        bind.scrollView.visibility = View.GONE
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            hideOrShowImage(View.GONE)
                        bind.errorLayout.visibility = View.VISIBLE
                        bind.buttonReload.visibility = View.VISIBLE
                        bind.progressBar.visibility = View.GONE
                    }
                }
            }
        }
        bind.buttonReload.setOnClickListener {
            viewModel.reloadFilm()
        }
    }

    fun hideOrShowImage(mode: Int) {
        bind.poster.visibility = mode
        bind.linearLayout.visibility = mode
        bind.backgroundInfo3.visibility = mode
        bind.backgroundInfo2.visibility = mode
        bind.backgroundInfo1.visibility = mode
        bind.logo.visibility = mode
        bind.backgroundBigName.visibility = mode
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
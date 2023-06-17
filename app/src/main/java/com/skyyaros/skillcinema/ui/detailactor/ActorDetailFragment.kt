package com.skyyaros.skillcinema.ui.detailactor

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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
import com.skyyaros.skillcinema.databinding.ActorDetailFragmentBinding
import com.skyyaros.skillcinema.entity.DetailActor
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.LeftSpaceDecorator
import com.skyyaros.skillcinema.ui.home.FilmPreviewAdapter
import com.skyyaros.skillcinema.ui.home.FilmPreviewAllAdapter
import java.util.Locale

class ActorDetailFragment: Fragment() {
    private var _bind: ActorDetailFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: ActorDetailFragmentArgs by navArgs()
    private val viewModel: ActorDetailViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActorDetailViewModel(App.component.getKinopoiskRepository(), args.id) as T
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = ActorDetailFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.showUpBar("")
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.detailActorFlow.collect { stateDetailActor ->
                when (stateDetailActor) {
                    is StateDetailActor.Loading -> {
                        bind.scrollView.visibility = View.GONE
                        bind.errorLayout.visibility = View.GONE
                        bind.buttonReload.visibility = View.GONE
                        bind.progressBar.visibility = View.VISIBLE
                    }
                    is StateDetailActor.Success -> {
                        bind.scrollView.visibility = View.VISIBLE
                        bind.errorLayout.visibility = View.GONE
                        bind.buttonReload.visibility = View.GONE
                        bind.progressBar.visibility = View.GONE
                        val item = stateDetailActor.data
                        setupActor(item)
                        setupFacts(item)
                        setupFilms(item)
                    }
                    is StateDetailActor.Error -> {
                        bind.scrollView.visibility = View.GONE
                        bind.errorLayout.visibility = View.VISIBLE
                        bind.buttonReload.visibility = View.VISIBLE
                        bind.progressBar.visibility = View.GONE
                    }
                }
            }
        }
        bind.buttonReload.setOnClickListener {
            viewModel.reloadActor()
        }
    }

    private fun setupActor(item: DetailActor) {
        val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            resources.getDrawable(R.drawable.empty_night)
        } else {
            resources.getDrawable(R.drawable.empty)
        }
        Glide.with(bind.photo.context).load(item.posterUrl).placeholder(placeholderId).into(bind.photo)
        bind.name.text = if (Locale.getDefault().language == "ru") {
            if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else
                getString(R.string.home_text_no_name)
        } else {
            if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else
                getString(R.string.home_text_no_name)
        }
        if (item.profession != null) {
            bind.profi.text = item.profession
        }
        val ageText = StringBuilder("")
        if (item.age != null && item.age > 0) {
            val form = if (Locale.getDefault().language == "ru") {
                if (item.age in 11..19)
                    "лет"
                else if (item.age % 10 in 2..4)
                    "года"
                else if (item.age % 10 == 1)
                    "год"
                else
                    "лет"
            } else {
                "years old"
            }
            ageText.append("${item.age} $form")
        }
        if (item.birthday != null) {
            ageText.append(", ${item.birthday}")
            if (item.birthplace != null) {
                ageText.append(" (${item.birthplace})")
            }
            if (item.death != null) {
                ageText.append(" - ${item.death}")
                if (item.deathplace != null) {
                    ageText.append(" (${item.deathplace})")
                }
            }
        }
        bind.age.text = ageText.toString()
        if (item.hasAwards != null && item.hasAwards >= 1) {
            bind.priz.text = getString(R.string.detail_text_priz, item.hasAwards)
        }
    }

    private fun setupFacts(item: DetailActor) {
        if (item.facts != null && item.facts.isNotEmpty()) {
            val _facts = StringBuilder("")
            item.facts.forEach {
                _facts.append("$it\n")
            }
            val facts = _facts.toString().removeSuffix("\n")

            val maxLinesCollapsed = 3
            bind.facts.setOnClickListener {
                if (viewModel.animationActive) {
                    bind.parentLayout.layoutTransition = bind.parentLayout.layoutTransition
                }
                viewModel.animationActive = true
                if (viewModel.isCollapsing) {
                    bind.facts.maxLines = Int.MAX_VALUE
                } else {
                    bind.facts.maxLines = maxLinesCollapsed
                }
                viewModel.isCollapsing = !viewModel.isCollapsing
            }

            if (viewModel.isCollapsing) {
                bind.facts.maxLines = maxLinesCollapsed
            } else {
                bind.facts.maxLines = Int.MAX_VALUE
            }
            bind.facts.text = facts
            bind.facts.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (bind.facts.maxLines == Int.MAX_VALUE) {
                        if (bind.facts.lineCount <= maxLinesCollapsed) {
                            bind.facts.isClickable = false
                            bind.facts.ellipsize = null
                        } else {
                            bind.facts.isClickable = true
                            bind.facts.ellipsize = TextUtils.TruncateAt.END
                        }
                    } else {
                        if (bind.facts.lineCount <= bind.facts.maxLines) {
                            bind.facts.isClickable = false
                            bind.facts.ellipsize = null
                        } else {
                            bind.facts.isClickable = true
                            bind.facts.ellipsize = TextUtils.TruncateAt.END
                        }
                    }
                    bind.facts.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
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
            bind.parentLayout.layoutTransition = transition
        } else {
            bind.facts.visibility = View.GONE
        }
    }

    private fun setupFilms(item: DetailActor) {
        if (item.best10Films != null && item.best10Films!!.isNotEmpty()) {
            val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.small_margin).toInt(), false)
            val leftMargin = LeftSpaceDecorator(requireContext().resources.getDimension(R.dimen.big_margin).toInt())
            val onClickList: ()->Unit = {
                val action = ActorDetailFragmentDirections.actionActorDetailFragmentToListpageFragment(
                    null,
                    8,
                    -1, null,
                    -1, null,
                    item.listBestFilmPreviewHalf!!.toTypedArray()
                )
                findNavController().navigate(action)
            }
            val adapter = FilmPreviewAdapter(item.best10Films!!, requireContext()) {
                val action = ActorDetailFragmentDirections.actionActorDetailFragmentToDetailFilmFragment(it)
                findNavController().navigate(action)
            }
            if (item.listBestFilmPreviewHalf!!.size > 10) {
                val allAdapter = FilmPreviewAllAdapter { onClickList() }
                bind.listFilms.adapter = ConcatAdapter(adapter, allAdapter)
            } else {
                bind.listFilms.adapter = adapter
            }
            if (bind.listFilms.itemDecorationCount == 0) {
                bind.listFilms.addItemDecoration(itemMargin)
                bind.listFilms.addItemDecoration(leftMargin)
            }
            if (item.listBestFilmPreviewHalf!!.size > 10) {
                bind.textCountFilms.setOnClickListener { onClickList() }
            } else {
                bind.textCountFilms.visibility = View.GONE
            }
        } else {
            bind.layoutFilms.visibility = View.GONE
            bind.listFilms.visibility = View.GONE
        }

        val allFilms = item.listFilmPreviewHalf?.distinctBy { it.filmId }
        if (allFilms != null && allFilms.isNotEmpty()) {
            bind.countFilms.text = if (Locale.getDefault().language == "ru") {
                val form = if (allFilms.size in 11..19)
                    "фильмов"
                else if (allFilms.size % 10 in 2..4)
                    "фильма"
                else if (allFilms.size % 10 == 1)
                    "фильм"
                else
                    "фильмов"
                "${allFilms.size} $form"
            } else {
                "${allFilms.size} films"
            }
            bind.layoutAllFilms.setOnClickListener {
                val action = ActorDetailFragmentDirections.actionActorDetailFragmentToFilmographyFragment(
                    item.listFilmPreviewHalf.toTypedArray(),
                    if (Locale.getDefault().language == "ru")
                        item.nameRu ?: (item.nameEn ?: getString(R.string.home_text_no_name))
                    else
                        item.nameEn ?: (item.nameRu ?: getString(R.string.home_text_no_name))
                )
                findNavController().navigate(action)
            }
        } else {
            bind.layoutAllFilms.visibility = View.GONE
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
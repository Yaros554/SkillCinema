package com.skyyaros.skillcinema.ui.listpage

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
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
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.ListpageFragmentBinding
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.home.FilmPreviewAdapter

class ListpageFragment: Fragment() {
    private var _bind: ListpageFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: ListpageFragmentArgs by navArgs()
    private val viewModel: ListpageViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ListpageViewModel(App.component.getKinopoiskRepository(), args.mode, args.countryId, args.genreId, args.listPreload?.toList(), args.listHalf?.toList()) as T
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = ListpageFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onClickFilm: (Long) -> Unit = {
            val action = ListpageFragmentDirections.actionListpageFragmentToDetailFilmFragment(it)
            findNavController().navigate(action)
        }
        activityCallbacks!!.showDownBar()
        val label = when (args.mode) {
            1 -> getString(R.string.home_text_premier)
            2 -> getString(R.string.home_text_populars)
            4 -> getString(R.string.home_text_top)
            6 -> getString(R.string.home_text_serials)
            7 -> getString(R.string.detail_text_similar)
            8 -> getString(R.string.detail_text_best)
            else -> "${args.genreName}, ${args.countryName}"
        }
        activityCallbacks!!.showUpBar(label)
        if (args.mode == 1) {
            val adapter = FilmPreviewAdapter(args.listPreload!!.toList(), requireContext(), onClickFilm)
            bind.recyclerView.adapter = adapter
        } else {
            val adapter = ListpageAdapter(requireContext(), onClickFilm)
            bind.recyclerView.adapter = adapter.withLoadStateFooter(MyLoadStateAdapter {
                adapter.retry()
            })
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.pagedFilms.collect {
                    adapter.submitData(it)
                }
            }
        }
        if (bind.recyclerView.itemDecorationCount == 0) {
            val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.big_margin).toInt(), false)
            bind.recyclerView.addItemDecoration(itemMargin)
        }
        val recyclerViewSize = Resources.getSystem().displayMetrics.widthPixels
        val groupSize = if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            requireContext().resources.getDimension(R.dimen.port_film_margin).toInt()
        else
            requireContext().resources.getDimension(R.dimen.land_film_margin).toInt()
        val padding = (recyclerViewSize - groupSize) / 2
        bind.recyclerView.setPadding(padding, 0, padding, 0)
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
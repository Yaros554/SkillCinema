package com.skyyaros.skillcinema.ui.dopsearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SearchGenreCountryFragmentBinding
import com.skyyaros.skillcinema.entity.SearchQuery
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.SearchSettingsViewModel
import kotlinx.coroutines.flow.collect

class SearchGenreCountryFragment: Fragment() {
    private var _bind: SearchGenreCountryFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: SearchGenreCountryFragmentArgs by navArgs()
    private val viewModel: SearchGenreCountryViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchGenreCountryViewModel(args.mode, App.component.getKinopoiskRepository()) as T
            }
        }
    }
    private val sharedViewModel: SearchSettingsViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = SearchGenreCountryFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.showUpBar(
            if (args.mode == 1)
                getString(R.string.search_set_title_genre)
            else
                getString(R.string.search_set_title_country)
        )
        bind.editText.hint = getString(if (args.mode == 1) R.string.search_set_edit_text_genre else R.string.search_set_edit_text_country)
        val adapter = GenreCountryAdapter(args.id, requireContext()) {
            val mode = SearchGenreCountryMode.values()[args.mode]
            val newSetting = if (mode == SearchGenreCountryMode.COUNTRY)
                SearchSettings(countryId = it, type = TypeSettings.COUNTRY)
            else
                SearchSettings(genreId = it, type = TypeSettings.GENRE)
            sharedViewModel.emitSearchSettings(newSetting)
            requireActivity().onBackPressed()
        }
        bind.recyclerView.adapter = adapter
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_color))
        bind.recyclerView.addItemDecoration(divider)
        bind.editText.addTextChangedListener (
            { _, _, _, _ -> },
            { text, _, _, _ ->
                viewModel.emitData(text.toString())
            },
            { }
        )
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.genreCountryFlow.collect {
                val data = listOf(GenreOrCountry(-1L, getString(R.string.search_set_text_matter))) + it
                adapter.submitList(data)
                if (viewModel.isInit) {
                    val pos = adapter.currentList.indexOfFirst { genreOrCountry ->
                        genreOrCountry.id == args.id
                    }
                    if (pos != -1)
                        bind.recyclerView.scrollToPosition(pos)
                    viewModel.isInit = false
                }
                if (it.isEmpty()) {
                    bind.recyclerView.visibility = View.GONE
                    bind.noResult.visibility = View.VISIBLE
                }
                else {
                    bind.noResult.visibility = View.GONE
                    bind.recyclerView.visibility = View.VISIBLE
                }
            }
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

enum class SearchGenreCountryMode {
    COUNTRY, GENRE
}
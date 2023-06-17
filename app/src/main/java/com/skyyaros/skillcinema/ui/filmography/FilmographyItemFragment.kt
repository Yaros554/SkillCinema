package com.skyyaros.skillcinema.ui.filmography

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FilmographyItemFragmentBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.photography.MyLoadStateAdapterTwo
import com.skyyaros.skillcinema.ui.photography.PhotographyItemFragment

class FilmographyItemFragment: Fragment() {
    private var _bind: FilmographyItemFragmentBinding? = null
    private val bind get() = _bind!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = FilmographyItemFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = (parentFragment as FilmographyFragment).viewModel
        val onClick = (parentFragment as FilmographyFragment).onClick
        val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.small_margin).toInt(), false)
        val adapter = FilmPreviewTwoAdapter(requireContext(), onClick)
        bind.recycler.adapter = adapter.withLoadStateFooter(MyLoadStateAdapterTwo{})
        if (bind.recycler.itemDecorationCount == 0) {
            bind.recycler.addItemDecoration(itemMargin)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pagedFilms[requireArguments().getString(argsBundle, "")]!!.collect {
                adapter.submitData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect {
                if (it.refresh == LoadState.Loading && adapter.itemCount == 0) {
                    bind.progressBar.visibility = View.VISIBLE
                } else {
                    bind.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    companion object {
        private const val argsBundle = "FilmographyItemFragmentBundle"

        fun create(itemType: String): FilmographyItemFragment {
            val fragment = FilmographyItemFragment()
            val bundle = Bundle(1)
            bundle.putString(argsBundle, itemType)
            fragment.arguments = bundle
            return fragment
        }
    }
}
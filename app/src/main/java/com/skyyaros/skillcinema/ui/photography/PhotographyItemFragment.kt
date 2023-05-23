package com.skyyaros.skillcinema.ui.photography

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotographyItemFragmentBinding
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration

class PhotographyItemFragment: Fragment() {
    private var _bind: PhotographyItemFragmentBinding? = null
    private val bind get() = _bind!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = PhotographyItemFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = (parentFragment as PhotographyFragment).viewModel
        val orientation = requireContext().resources.configuration.orientation
        val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.small_margin).toInt(), false, if (orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 0)
        val adapter = PhotoPreviewTwoAdapter(requireContext(), orientation)
        bind.recycler.adapter = adapter.withLoadStateFooter(MyLoadStateAdapterTwo { adapter.retry() })
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
            spanSizeLookup = object: SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % 3 == 2 && orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 1
                }
            }
        }
        bind.recycler.layoutManager = gridLayoutManager
        if (bind.recycler.itemDecorationCount == 0) {
            bind.recycler.addItemDecoration(itemMargin)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pagedPhotos[requireArguments().getString(argsBundle, "")]!!.collect {
                adapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    companion object {
        private const val argsBundle = "PhotographyItemFragmentBundle"

        fun create(itemType: String): PhotographyItemFragment {
            val fragment = PhotographyItemFragment()
            val bundle = Bundle(1)
            bundle.putString(argsBundle, itemType)
            fragment.arguments = bundle
            return fragment
        }
    }
}
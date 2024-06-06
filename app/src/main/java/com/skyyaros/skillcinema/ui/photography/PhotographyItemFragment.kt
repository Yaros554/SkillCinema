package com.skyyaros.skillcinema.ui.photography

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotographyItemFragmentBinding
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

class PhotographyItemFragment: Fragment() {
    private var _bind: PhotographyItemFragmentBinding? = null
    val bind get() = _bind!!
    lateinit var adapter: PhotoPreviewTwoAdapter
    private val goToPhotos: (String)->Unit = { curUrl ->
        (parentFragment as PhotographyFragment).goToPhotos(requireArguments().getString(argsBundle, ""), curUrl)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = PhotographyItemFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myParentFr = parentFragment as PhotographyFragment
        val arg = requireArguments().getString(argsBundle, "")
        if (myParentFr.viewModel.needUpdate[arg]!!) {
            myParentFr.viewModel.updatePagingData(requireArguments().getString(argsBundle, ""))
        }
        val orientation = requireContext().resources.configuration.orientation
        val itemMargin = AdaptiveSpacingItemDecoration(
            requireContext().resources.getDimension(R.dimen.small_margin).toInt(),
            false,
            if (orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 0
        )
        val currentUrl = myParentFr.activityCallbacks!!.getUrlPosAnim(myParentFr.args.stack)
        adapter = PhotoPreviewTwoAdapter(requireContext(), orientation, myParentFr, currentUrl, goToPhotos)
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
            myParentFr.viewModel.pagedPhotos[requireArguments().getString(argsBundle, "")]!!.collectLatest {
                adapter.submitData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }.filter { it.refresh is LoadState.NotLoading }.collect {
                if (myParentFr.viewModel.needUpdate[arg]!!) {
                    val curUrl = myParentFr.activityCallbacks!!.getUrlPosAnim(myParentFr.args.stack)
                    val pos = adapter.snapshot().items.indexOfFirst { it.imageUrl == curUrl }
                    bind.recycler.scrollToPosition(pos)
                    delay(500)
                    myParentFr.viewModel.enablePreload(arg)
                    myParentFr.viewModel.needUpdate[arg] = false
                }
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
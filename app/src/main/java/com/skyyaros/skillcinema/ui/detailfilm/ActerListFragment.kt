package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.ActerListFragmentBinding
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class ActerListFragment: Fragment() {
    private var _bind: ActerListFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: ActerListFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = ActerListFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onClick: (Long) -> Unit = { id ->
            val action = ActerListFragmentDirections.actionActerListFragmentToActorDetailFragment(id, args.stack)
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
        activityCallbacks!!.showDownBar()
        activityCallbacks!!.showUpBar(args.role)
        val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.big_margin).toInt(), false)
        val adapter = ActerPreviewAdapter(args.listPreload.toList(), requireContext(), onClick)
        bind.recyclerView.adapter = adapter
        bind.recyclerView.addItemDecoration(itemMargin)
        val density = requireContext().resources.displayMetrics.density
        var recyclerViewSize = Resources.getSystem().displayMetrics.widthPixels
        if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            recyclerViewSize -= (72*density).toInt()
        val groupSize = if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            requireContext().resources.getDimension(R.dimen.port_actor_margin).toInt()
        else
            requireContext().resources.getDimension(R.dimen.land_actor_margin).toInt()
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
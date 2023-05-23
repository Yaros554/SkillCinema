package com.skyyaros.skillcinema.ui.filmography

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FilmographyFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class FilmographyFragment: Fragment() {
    private var _bind: FilmographyFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: FilmographyFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = FilmographyFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.showUpBar(getString(R.string.detail_text_all_films))
        bind.actorName.text = args.nameActor
        val items = args.listFilms.toList().groupBy { it.professionKey!! }.map { it.value.distinctBy { item -> item.filmId } }
        val adapter = FilmographyItemAdapter(
            items, requireContext(),
            {
                val action = FilmographyFragmentDirections.actionFilmographyFragmentToDetailFilmFragment(it)
                findNavController().navigate(action)
            },
            activityCallbacks!!.getMainViewModel(),
            viewLifecycleOwner.lifecycleScope
        )
        bind.viewPager.adapter = adapter
        TabLayoutMediator(bind.tabs, bind.viewPager) { tab, position ->
            tab.text = "${items[position].first().professionKey!!} ${items[position].size}"
        }.attach()
        for (i in 0 until bind.tabs.tabCount) {
            val tab = (bind.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as MarginLayoutParams
            if (i == 0) {
                p.setMargins(16, 0, 16, 0)
            } else {
                p.setMargins(0, 0, 16, 0)
            }
            tab.requestLayout()
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
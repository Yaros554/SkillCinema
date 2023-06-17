package com.skyyaros.skillcinema.ui.filmography

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skyyaros.skillcinema.entity.FilmPreviewHalf

class FilmographyItemAdapter(
    private val items: List<List<FilmPreviewHalf>>,
    parentFragment: FilmographyFragment
): FragmentStateAdapter(parentFragment) {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return FilmographyItemFragment.create(items[position].first().professionKey!!)
    }
}
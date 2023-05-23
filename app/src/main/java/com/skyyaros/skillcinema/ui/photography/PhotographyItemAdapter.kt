package com.skyyaros.skillcinema.ui.photography

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skyyaros.skillcinema.entity.ImageResponse

class PhotographyItemAdapter(private val items: List<ImageResponse>, parentFragment: PhotographyFragment): FragmentStateAdapter(parentFragment) {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return PhotographyItemFragment.create(items[position].imageType!!)
    }
}
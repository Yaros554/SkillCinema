package com.skyyaros.skillcinema.ui.dopsearch

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class YearAdapter(
    private val items: List<List<Int>>,
    private val mode: Int,
    parentFragment: SearchYearFragment
): FragmentStateAdapter(parentFragment) {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return YearFragment.create(items[position], mode)
    }
}
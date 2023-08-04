package com.skyyaros.skillcinema.ui.video

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skyyaros.skillcinema.entity.VideoItem

class VideoItemAdapter(private val items: List<VideoItem>, parentActivity: VideoActivity): FragmentStateAdapter(parentActivity) {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return VideoItemFragment.create(items[position].url.substringAfterLast('/'))
    }
}
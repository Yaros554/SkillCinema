package com.skyyaros.skillcinema.ui.video

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skyyaros.skillcinema.entity.VideoItem

class VideoItemAdapter(private val items: List<VideoItem>, parentActivity: VideoActivity): FragmentStateAdapter(parentActivity) {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (items[position].site == "YOUTUBE")
            VideoItemFragment.create(items[position].url.substringAfterLast('/'))
        else
            VideoItemFragmentKnpsk.create(items[position].url)
    }
}
package com.skyyaros.skillcinema.ui.detailfilm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.VideoPreviewBinding
import com.skyyaros.skillcinema.entity.VideoItem

class VideoPreviewAdapter(
    private val items: List<VideoItem>,
    private val lifecycle: Lifecycle,
    private val onClick: (String)->Unit
): RecyclerView.Adapter<VideoPreviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPreviewHolder {
        val binding = VideoPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = VideoPreviewHolder(binding, lifecycle)
        binding.blockPlayer.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onClick(items[pos].url.substringAfterLast('/'))
            }
        }
        return myHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VideoPreviewHolder, position: Int) {
        holder.bind(items[position].url.substringAfterLast('/'))
    }

}
package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.VideoPreviewBinding
import com.skyyaros.skillcinema.entity.VideoItem

class VideoPreviewHolder(
    private val binding: VideoPreviewBinding,
    private val posterUrl: String,
    private val context: Context
): RecyclerView.ViewHolder(binding.root) {
    fun bind(video: VideoItem) {
        binding.service.setImageDrawable(
            if (video.site == "YOUTUBE")
                context.getDrawable(R.drawable.youtube)
            else
                context.getDrawable(R.drawable.kinopoisk)
        )
        Glide.with(context).load(posterUrl).into(binding.poster)
    }
}
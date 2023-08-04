package com.skyyaros.skillcinema.ui.detailfilm

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.skyyaros.skillcinema.databinding.VideoPreviewBinding

class VideoPreviewHolder(binding: VideoPreviewBinding, lifecycle: Lifecycle): RecyclerView.ViewHolder(binding.root) {
    private var youTubePlayer: YouTubePlayer? = null
    private var currentVideoId: String? = null

    init {
        lifecycle.addObserver(binding.videoPlayer)
        binding.videoPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // store youtube player reference for later
                this@VideoPreviewHolder.youTubePlayer = youTubePlayer
                // cue the video if it's available
                currentVideoId?.let { youTubePlayer.cueVideo(it, 0f) }
            }
        })
    }

    fun bind(videoId: String) {
        currentVideoId = videoId
        // cue the video if the youtube player is available
        youTubePlayer?.cueVideo(videoId, 0f)
    }
}
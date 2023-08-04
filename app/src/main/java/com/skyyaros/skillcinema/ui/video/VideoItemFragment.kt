package com.skyyaros.skillcinema.ui.video

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.skyyaros.skillcinema.databinding.VideoItemFragmentBinding

class VideoItemFragment: Fragment() {
    private var _bind: VideoItemFragmentBinding? = null
    private val bind get() = _bind!!
    private var myYouTubePlayer: YouTubePlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = VideoItemFragmentBinding.inflate(inflater, container, false)
        if (requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            bind.videoPlayer.matchParent()
        lifecycle.addObserver(bind.videoPlayer)
        bind.videoPlayer.addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                val videoId = requireArguments().getString(argsBundle, "")
                youTubePlayer.cueVideo(videoId, 0.0f)
                myYouTubePlayer = youTubePlayer
            }
        })
        return bind.root
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            bind.videoPlayer.wrapContent()
        else
            bind.videoPlayer.matchParent()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (!menuVisible)
            myYouTubePlayer?.pause()
    }

    companion object {
        private const val argsBundle = "VideoItemFragmentBundle"

        fun create(videoId: String): VideoItemFragment {
            val fragment = VideoItemFragment()
            val bundle = Bundle(1)
            bundle.putString(argsBundle, videoId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
package com.skyyaros.skillcinema.ui.video

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.skyyaros.skillcinema.databinding.VideoItemFragmentKnpskBinding
import kotlin.math.min

class VideoItemFragmentKnpsk: Fragment() {
    private var _bind: VideoItemFragmentKnpskBinding? = null
    private val bind get() = _bind!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = VideoItemFragmentKnpskBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val disp = (requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = min(disp.width, disp.height)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            bind.videoPlayerKnpsk.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = size
            }
        else
            bind.videoPlayerKnpsk.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        val videoUrl = requireArguments().getString(argsBundle, "")
        bind.videoPlayerKnpsk.webViewClient = MyWebViewClient()
        bind.videoPlayerKnpsk.getSettings().setJavaScriptEnabled(true)
        bind.videoPlayerKnpsk.loadUrl(videoUrl)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val disp = (requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = min(disp.width, disp.height)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            bind.videoPlayerKnpsk.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = size
            }
        else
            bind.videoPlayerKnpsk.layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    companion object {
        private const val argsBundle = "VideoItemFragmentKnpskBundle"

        fun create(videoUrl: String): VideoItemFragmentKnpsk {
            val fragment = VideoItemFragmentKnpsk()
            val bundle = Bundle(1)
            bundle.putString(argsBundle, videoUrl)
            fragment.arguments = bundle
            return fragment
        }
    }
}

private class MyWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        view.loadUrl(request.url.toString())
        return true
    }
}
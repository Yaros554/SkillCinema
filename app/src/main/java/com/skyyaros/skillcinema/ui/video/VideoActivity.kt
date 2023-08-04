package com.skyyaros.skillcinema.ui.video

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.VideoActivityBinding
import com.skyyaros.skillcinema.entity.VideoItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LIST_ITEMS = "com.skyyaros.skillcinema.ui.listItems"
private const val CUR_ITEM = "com.skyyaros.skillcinema.ui.curItem"

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: VideoActivityBinding
    private var myCoroutine: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VideoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
        val pToolbar = binding.toolbar.layoutParams as ViewGroup.MarginLayoutParams
        pToolbar.setMargins(0, statusBarHeight, 0, 0)
        binding.toolbar.requestLayout()
        val a = TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, a, true)
        binding.toolbar.setBackgroundColor(a.data)

        val tempItems: ArrayList<VideoItem> = intent.getParcelableArrayListExtra(LIST_ITEMS)!!
        val items = tempItems.toList()
        val curItem = intent.getStringExtra(CUR_ITEM)
        val adapter = VideoItemAdapter(items, this)
        binding.viewPager.adapter = adapter
        val index = items.indexOfFirst { it.url.substringAfterLast('/') == curItem }
        binding.viewPager.setCurrentItem(index, false)

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.toolbar.title = ""
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            fullScreenOff()
        else
            fullScreenOn()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            fullScreenOn()
        else
            fullScreenOff()
    }

    private fun fullScreenOn() {
        myCoroutine?.cancel()
        myCoroutine = lifecycleScope.launch {
            binding.toolbar.visibility = View.GONE
            delay(300)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    private fun fullScreenOff() {
        myCoroutine?.cancel()
        myCoroutine = lifecycleScope.launch {
            val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            window.decorView.systemUiVisibility = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            else
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            delay(300)
            binding.toolbar.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newIntent(packageContext: Context, listItems: List<VideoItem>, curItem: String): Intent {
            return Intent(packageContext, VideoActivity::class.java).apply {
                val arrayListItems = ArrayList(listItems)
                putParcelableArrayListExtra(LIST_ITEMS, arrayListItems)
                putExtra(CUR_ITEM, curItem)
            }
        }
    }
}
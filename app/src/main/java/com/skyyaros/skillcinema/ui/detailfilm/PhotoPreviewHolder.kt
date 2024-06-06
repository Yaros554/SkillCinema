package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotoPreviewBinding
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class PhotoPreviewHolder(
    val binding: PhotoPreviewBinding,
    private val context: Context,
    private val fragment: Fragment,
    private val activityCallbacks: ActivityCallbacks,
    private val stack: String
    ): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItem) {
        binding.photo.transitionName = item.imageUrl
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.getDrawable(R.drawable.empty_night)
        } else {
            context.getDrawable(R.drawable.empty)
        }
        Glide
            .with(binding.photo.context)
            .load(item.previewUrl)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?,
                                          target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    if (item.imageUrl == activityCallbacks.getUrlPosAnim(stack))
                        fragment.startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?,
                                             target: Target<Drawable>?, dataSource: DataSource?,
                                             isFirstResource: Boolean): Boolean {
                    if (item.imageUrl == activityCallbacks.getUrlPosAnim(stack))
                        fragment.startPostponedEnterTransition()
                    return false
                }
            })
            .placeholder(placeholderId)
            .into(binding.photo)
    }
}
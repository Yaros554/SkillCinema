package com.skyyaros.skillcinema.ui.fullphoto

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FullPhotoFrAndVpBinding
import com.skyyaros.skillcinema.entity.ImageItem

class FullPhotoHolder(
    val binding: FullPhotoFrAndVpBinding,
    private val context: Context,
    private val fragment: Fragment
    ): RecyclerView.ViewHolder(binding.root) {
    private val myRequestListener = MyRequestListener()
    fun bind(item: ImageItem) {
        binding.imageView.transitionName = item.imageUrl
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.resources.getDrawable(R.drawable.empty_night)
        } else {
            context.resources.getDrawable(R.drawable.empty)
        }
        Glide
            .with(binding.imageView.context)
            .load(item.imageUrl)
            .listener(myRequestListener)
            .thumbnail(
                Glide
                    .with(binding.imageView.context)
                    .load(item.previewUrl)
                    .priority(Priority.IMMEDIATE)
                    .listener(myRequestListener)
            )
            .placeholder(placeholderId)
            .into(binding.imageView)
    }

    private inner class MyRequestListener: RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?, model: Any?,
            target: Target<Drawable>?, isFirstResource: Boolean
        ): Boolean {
            fragment.startPostponedEnterTransition()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?, model: Any?,
            target: Target<Drawable>?, dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            fragment.startPostponedEnterTransition()
            return false
        }
    }
}
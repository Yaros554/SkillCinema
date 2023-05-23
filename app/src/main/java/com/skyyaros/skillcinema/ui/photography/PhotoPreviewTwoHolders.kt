package com.skyyaros.skillcinema.ui.photography

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotoPreviewTwoBigBinding
import com.skyyaros.skillcinema.databinding.PhotoPreviewTwoSmallBinding
import com.skyyaros.skillcinema.entity.ImageItem

class PhotoPreviewTwoBigHolder(private val binding: PhotoPreviewTwoBigBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItem) {
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholder = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.resources.getDrawable(R.drawable.empty_night)
        } else {
            context.resources.getDrawable(R.drawable.empty)
        }
        Glide.with(binding.photo.context).load(item.previewUrl).placeholder(placeholder).into(binding.photo)
    }
}

class PhotoPreviewTwoSmallHolder(private val binding: PhotoPreviewTwoSmallBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItem) {
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholder = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.resources.getDrawable(R.drawable.empty_night)
        } else {
            context.resources.getDrawable(R.drawable.empty)
        }
        Glide.with(binding.photo.context).load(item.previewUrl).placeholder(placeholder).into(binding.photo)
    }
}
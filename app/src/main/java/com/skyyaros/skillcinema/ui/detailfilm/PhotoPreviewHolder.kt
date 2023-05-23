package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotoPreviewBinding
import com.skyyaros.skillcinema.entity.ImageItem

class PhotoPreviewHolder(private val binding: PhotoPreviewBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItem) {
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.getDrawable(R.drawable.empty_night)
        } else {
            context.getDrawable(R.drawable.empty)
        }
        Glide.with(binding.photo.context).load(item.previewUrl).placeholder(placeholderId).into(binding.photo)
    }
}
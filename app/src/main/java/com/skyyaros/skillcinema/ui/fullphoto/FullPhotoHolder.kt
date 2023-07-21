package com.skyyaros.skillcinema.ui.fullphoto

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FullPhotoFrAndVpBinding
import com.skyyaros.skillcinema.entity.ImageItem

class FullPhotoHolder(
    private val binding: FullPhotoFrAndVpBinding,
    private val context: Context
    ): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItem) {
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.resources.getDrawable(R.drawable.empty_night)
        } else {
            context.resources.getDrawable(R.drawable.empty)
        }
        //Glide.with(binding.imageView.context).load(item.imageUrl).placeholder(placeholderId).into(binding.imageView)
        Glide
            .with(binding.imageView.context)
            .load(item.imageUrl)
            .thumbnail(Glide.with(binding.imageView.context).load(item.previewUrl))
            .placeholder(placeholderId)
            .into(binding.imageView)
    }
}
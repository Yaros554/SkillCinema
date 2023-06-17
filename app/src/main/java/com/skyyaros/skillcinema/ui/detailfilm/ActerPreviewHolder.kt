package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.ActerPreviewBinding
import com.skyyaros.skillcinema.entity.ActorPreview
import java.util.Locale

class ActerPreviewHolder(private val binding: ActerPreviewBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ActorPreview) {
        binding.name.text = if (Locale.getDefault().language == "ru") {
            if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else
                context.getString(R.string.home_text_no_name)
        } else {
            if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else
                context.getString(R.string.home_text_no_name)
        }
        binding.role.text = item.description ?: item.professionText
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.getDrawable(R.drawable.empty_night)
        } else {
            context.getDrawable(R.drawable.empty)
        }
        Glide.with(binding.imageView.context).load(item.posterUrl).placeholder(placeholderId).into(binding.imageView)
    }
}
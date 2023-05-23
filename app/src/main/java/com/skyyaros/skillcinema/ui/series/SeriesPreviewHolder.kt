package com.skyyaros.skillcinema.ui.series

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SeriesPreviewBinding
import com.skyyaros.skillcinema.entity.Episode
import java.util.Locale

class SeriesPreviewHolder(private val binding: SeriesPreviewBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Episode) {
        val name = if (Locale.getDefault().language == "ru") {
            if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else
                ""
        } else {
            if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else
                ""
        }
        binding.name.text = "${context.getString(R.string.series_text_number, item.episodeNumber)} $name"
        if (!item.releaseDate.isNullOrBlank()) {
            binding.date.text = item.releaseDate
        } else {
            binding.date.visibility = View.GONE
        }
        if (!item.synopsis.isNullOrBlank()) {
            binding.description.text = item.synopsis
        } else {
            binding.description.visibility = View.GONE
        }
    }
}
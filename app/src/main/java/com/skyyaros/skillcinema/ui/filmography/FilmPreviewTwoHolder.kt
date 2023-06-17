package com.skyyaros.skillcinema.ui.filmography

import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FilmPreviewTwoBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import java.util.*

class FilmPreviewTwoHolder(private val binding: FilmPreviewTwoBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FilmPreview) {
        binding.name.text = if (Locale.getDefault().language == "ru") {
            if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else if (!item.nameOriginal.isNullOrBlank())
                item.nameOriginal
            else
                context.getString(R.string.home_text_no_name)
        } else {
            if (!item.nameEn.isNullOrBlank())
                item.nameEn
            else if (!item.nameOriginal.isNullOrBlank())
                item.nameOriginal
            else if (!item.nameRu.isNullOrBlank())
                item.nameRu
            else
                context.getString(R.string.home_text_no_name)
        }
        if (item.rating == null && item.ratingKinopoisk == null) {
            binding.rating.visibility = View.GONE
        } else {
            binding.rating.visibility = View.VISIBLE
            var temp = item.rating ?: (item.ratingKinopoisk ?: "")
            if (temp != "") {
                val chislo = temp.toDoubleOrNull()
                if (chislo == null) {
                    val chislo2 = temp.substring(0, temp.lastIndex).toDoubleOrNull()
                    if (chislo2 != null) {
                        temp = (chislo2 / 10).toString()
                    }
                }
            }
            binding.rating.text = temp
        }
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholder = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            context.getDrawable(R.drawable.empty_night)
        } else {
            context.getDrawable(R.drawable.empty)
        }
        binding.imageView.setImageDrawable(placeholder)
        Glide.with(binding.imageView.context).load(item.imageUrl).placeholder(placeholder).into(binding.imageView)
        val tempStr = StringBuilder("")
        if (item.year != null) {
            tempStr.append("${item.year}, ")
        }
        item.genres?.forEach {
            tempStr.append(it.genre)
            tempStr.append(", ")
        }
        binding.genre.text = tempStr.removeSuffix(", ").toString()
    }
}
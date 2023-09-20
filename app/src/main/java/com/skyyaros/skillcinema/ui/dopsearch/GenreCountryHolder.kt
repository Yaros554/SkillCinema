package com.skyyaros.skillcinema.ui.dopsearch

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SearchGenreCountryElBinding


class GenreCountryHolder(private val binding: SearchGenreCountryElBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(text: String, isSelect: Boolean) {
        binding.textView.text = text
        if (isSelect) {
            binding.root.setBackgroundColor(context.getColor(R.color.skill_grey2))
        } else {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
            @ColorInt val color = typedValue.data
            binding.root.setBackgroundColor(color)
        }
    }
}
package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.CategoryItemForFilmBinding
import com.skyyaros.skillcinema.entity.FilmActorTable

class CategoryItemForFilmHolder(
    private val binding: CategoryItemForFilmBinding,
    private val context: Context,
    private val filmId: Long
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: List<FilmActorTable>) {
        when(item[0].category) {
            "2"-> binding.checkBox.text = context.getString(R.string.profile_item_text2)
            "3"-> binding.checkBox.text = context.getString(R.string.profile_item_text)
            else-> binding.checkBox.text = item[0].category
        }
        binding.checkBox.isChecked = item.find { it.kinopoiskId == filmId } != null
        binding.textView.text = (item.filter { it.kinopoiskId >= 0 }.size).toString()
    }
}
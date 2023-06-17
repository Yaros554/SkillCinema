package com.skyyaros.skillcinema.ui.filmography

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.skyyaros.skillcinema.databinding.FilmPreviewTwoBinding
import com.skyyaros.skillcinema.entity.FilmPreview

class FilmPreviewTwoAdapter(
    private val context: Context,
    private val onClick: (Long)->Unit
): PagingDataAdapter<FilmPreview, FilmPreviewTwoHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewTwoHolder {
        val binding = FilmPreviewTwoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val filmPreviewTwoHolder = FilmPreviewTwoHolder(binding, context)
        binding.root.setOnClickListener {
            val position = filmPreviewTwoHolder.bindingAdapterPosition
            val item = getItem(position)
            if (item != null) {
                val id = item.kinopoiskId ?: item.filmId
                onClick(id!!)
            }
        }
        return filmPreviewTwoHolder
    }

    override fun onBindViewHolder(holder: FilmPreviewTwoHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }
}

class DiffUtilCallback: DiffUtil.ItemCallback<FilmPreview>() {
    override fun areItemsTheSame(oldItem: FilmPreview, newItem: FilmPreview): Boolean {
        return if (oldItem.kinopoiskId != null && newItem.kinopoiskId != null)
            oldItem.kinopoiskId == newItem.kinopoiskId
        else if (oldItem.filmId != null && newItem.filmId != null)
            oldItem.filmId == newItem.filmId
        else
            false
    }
    override fun areContentsTheSame(oldItem: FilmPreview, newItem: FilmPreview): Boolean = oldItem == newItem
}
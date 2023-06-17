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
        return FilmPreviewTwoHolder(FilmPreviewTwoBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun onBindViewHolder(holder: FilmPreviewTwoHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            val id = item.kinopoiskId ?: item.filmId
            holder.binding.root.setOnClickListener { onClick(id!!) }
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
package com.skyyaros.skillcinema.ui.listpage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.skyyaros.skillcinema.databinding.FilmPreviewBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.home.FilmPreviewHolder

class ListpageAdapter(
    private val context: Context,
    private val onClick: (Long)->Unit
    ): PagingDataAdapter<FilmPreview, FilmPreviewHolder>(DiffUtilCallback()) {
    override fun onBindViewHolder(holder: FilmPreviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            val id = item.kinopoiskId ?: item.filmId
            holder.binding.root.setOnClickListener { onClick(id!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewHolder {
        return FilmPreviewHolder(FilmPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
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
package com.skyyaros.skillcinema.ui.listpage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewHolder {
        val binding = FilmPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val filmPreviewHolder = FilmPreviewHolder(binding, context)
        binding.root.setOnClickListener {
            val position = filmPreviewHolder.bindingAdapterPosition
            val item = getItem(position)
            if (item != null) {
                val id = item.kinopoiskId ?: item.filmId
                onClick(id!!)
            }
        }
        return filmPreviewHolder
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
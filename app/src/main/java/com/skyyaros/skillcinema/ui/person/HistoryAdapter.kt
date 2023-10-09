package com.skyyaros.skillcinema.ui.person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.skyyaros.skillcinema.databinding.FilmPreviewBinding
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.home.FilmPreviewHolder

class HistoryAdapter(
    private val onClick: (FilmActorTable)->Unit
): ListAdapter<FilmActorTable, FilmPreviewHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewHolder {
        val binding = FilmPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = FilmPreviewHolder(binding, parent.context)
        binding.root.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            val item = getItem(pos)
            if (item != null) {
                onClick(item)
            }
        }
        return myHolder
    }

    override fun onBindViewHolder(holder: FilmPreviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            val visibleData = FilmPreview(
                item.kinopoiskId, null, item.posterUrlPreview,
                item.nameRu, item.nameEn, item.nameOriginal,
                item.genres, item.rating, null, null, null
            )
            holder.bind(visibleData)
        }
    }
    class DiffUtilCallback(): DiffUtil.ItemCallback<FilmActorTable>() {
        override fun areItemsTheSame(oldItem: FilmActorTable, newItem: FilmActorTable): Boolean {
            return oldItem.kinopoiskId == newItem.kinopoiskId && oldItem.category == newItem.category
        }

        override fun areContentsTheSame(oldItem: FilmActorTable, newItem: FilmActorTable): Boolean {
            return oldItem == newItem
        }
    }
}
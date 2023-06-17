package com.skyyaros.skillcinema.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FilmPreviewBinding
import com.skyyaros.skillcinema.entity.FilmPreview

class FilmPreviewAdapter(
    private val items: List<FilmPreview>,
    private val context: Context,
    private val onClick: (Long)->Unit
    ): RecyclerView.Adapter<FilmPreviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewHolder {
        val binding = FilmPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val filmPreviewHolder = FilmPreviewHolder(binding, context)
        binding.root.setOnClickListener {
            val position = filmPreviewHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val id = if (items[position].kinopoiskId != null)
                    items[position].kinopoiskId
                else
                    items[position].filmId
                onClick(id!!)
            }
        }
        return filmPreviewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FilmPreviewHolder, position: Int) {
        holder.bind(items[position])
    }
}
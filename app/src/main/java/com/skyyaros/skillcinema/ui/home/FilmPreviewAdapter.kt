package com.skyyaros.skillcinema.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FilmPreviewBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.MainViewModel

class FilmPreviewAdapter(
    private val items: List<FilmPreview>,
    private val context: Context,
    private val onClick: (Long)->Unit,
    private val viewModel: MainViewModel? = null,
    private val coroutineScope: LifecycleCoroutineScope? = null
    ): RecyclerView.Adapter<FilmPreviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewHolder {
        return FilmPreviewHolder(FilmPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FilmPreviewHolder, position: Int) {
        holder.bind(items[position], viewModel, coroutineScope)
        val id = if (items[position].kinopoiskId != null) items[position].kinopoiskId else items[position].filmId
        holder.binding.root.setOnClickListener { onClick(id!!) }
    }
}
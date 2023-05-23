package com.skyyaros.skillcinema.ui.filmography

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FilmPreviewTwoBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.MainViewModel

class FilmPreviewTwoAdapter(
    private val items: List<FilmPreview>,
    private val context: Context,
    private val onClick: (Long)->Unit,
    private val viewModel: MainViewModel,
    private val coroutineScope: LifecycleCoroutineScope
): RecyclerView.Adapter<FilmPreviewTwoHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewTwoHolder {
        return FilmPreviewTwoHolder(FilmPreviewTwoBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FilmPreviewTwoHolder, position: Int) {
        holder.bind(items[position], viewModel, coroutineScope)
        holder.binding.root.setOnClickListener { onClick(items[position].filmId!!) }
    }
}
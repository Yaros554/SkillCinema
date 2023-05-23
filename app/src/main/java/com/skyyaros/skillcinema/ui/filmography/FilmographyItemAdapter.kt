package com.skyyaros.skillcinema.ui.filmography

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FilmographyItemViewPagerBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.MainViewModel

class FilmographyItemAdapter(
    private val items: List<List<FilmPreview>>,
    private val context: Context,
    private val onClick: (Long)->Unit,
    private val viewModel: MainViewModel,
    private val coroutineScope: LifecycleCoroutineScope
): RecyclerView.Adapter<FilmographyItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmographyItemHolder {
        return FilmographyItemHolder(
            FilmographyItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            context, onClick, viewModel, coroutineScope
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FilmographyItemHolder, position: Int) {
        holder.bind(items[position])
    }
}
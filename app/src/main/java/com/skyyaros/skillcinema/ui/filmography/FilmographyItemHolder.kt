package com.skyyaros.skillcinema.ui.filmography

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FilmographyItemViewPagerBinding
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.MainViewModel

class FilmographyItemHolder(
    private val binding: FilmographyItemViewPagerBinding,
    private val context: Context,
    private val onClick: (Long)->Unit,
    private val viewModel: MainViewModel,
    private val coroutineScope: LifecycleCoroutineScope
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: List<FilmPreview>) {
        val itemMargin = AdaptiveSpacingItemDecoration(context.resources.getDimension(R.dimen.small_margin).toInt(), false)
        val adapter = FilmPreviewTwoAdapter(item, context, onClick, viewModel, coroutineScope)
        binding.recycler.adapter = adapter
        if (binding.recycler.itemDecorationCount == 0) {
            binding.recycler.addItemDecoration(itemMargin)
        }
    }
}
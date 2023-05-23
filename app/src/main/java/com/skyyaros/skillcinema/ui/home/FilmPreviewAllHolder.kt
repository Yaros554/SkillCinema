package com.skyyaros.skillcinema.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FilmPreviewAllBinding

class FilmPreviewAllHolder(private val binding: FilmPreviewAllBinding, onClick: () -> Unit): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.imageButton.setOnClickListener { onClick() }
    }
}
package com.skyyaros.skillcinema.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FilmPreviewAllBinding

class FilmPreviewAllAdapter(private val onClick: () -> Unit): RecyclerView.Adapter<FilmPreviewAllHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmPreviewAllHolder {
        return FilmPreviewAllHolder(FilmPreviewAllBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: FilmPreviewAllHolder, position: Int) { }

}
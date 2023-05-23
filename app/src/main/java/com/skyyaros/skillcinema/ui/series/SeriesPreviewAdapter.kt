package com.skyyaros.skillcinema.ui.series

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.SeriesPreviewBinding
import com.skyyaros.skillcinema.entity.Episode

class SeriesPreviewAdapter(private val items: List<Episode>, private val context: Context): RecyclerView.Adapter<SeriesPreviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesPreviewHolder {
        return SeriesPreviewHolder(SeriesPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SeriesPreviewHolder, position: Int) {
        holder.bind(items[position])
    }
}
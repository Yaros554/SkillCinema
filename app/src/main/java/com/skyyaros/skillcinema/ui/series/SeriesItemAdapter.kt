package com.skyyaros.skillcinema.ui.series

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.SeriesItemViewPagerBinding
import com.skyyaros.skillcinema.entity.Season

class SeriesItemAdapter(private val items: List<Season>, private val context: Context): RecyclerView.Adapter<SeriesItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesItemHolder {
        return SeriesItemHolder(SeriesItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SeriesItemHolder, position: Int) {
        holder.bind(items[position])
    }
}
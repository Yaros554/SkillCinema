package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.ActerPreviewBinding
import com.skyyaros.skillcinema.entity.ActorPreview

class ActerPreviewAdapter(
    private val items: List<ActorPreview>,
    private val context: Context,
    private val onClick: (Long)->Unit
    ): RecyclerView.Adapter<ActerPreviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActerPreviewHolder {
        return ActerPreviewHolder(
            ActerPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            context,
            onClick
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ActerPreviewHolder, position: Int) {
        holder.bind(items[position])
    }
}
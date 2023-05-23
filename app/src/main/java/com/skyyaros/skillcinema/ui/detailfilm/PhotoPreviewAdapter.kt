package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.PhotoPreviewBinding
import com.skyyaros.skillcinema.entity.ImageItem

class PhotoPreviewAdapter(private val items: List<ImageItem>, private val context: Context): RecyclerView.Adapter<PhotoPreviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoPreviewHolder {
        return PhotoPreviewHolder(PhotoPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false), context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PhotoPreviewHolder, position: Int) {
        holder.bind(items[position])
    }
}
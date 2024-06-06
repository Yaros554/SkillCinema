package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.PhotoPreviewBinding
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class PhotoPreviewAdapter(
    private val items: List<ImageItem>,
    private val context: Context,
    private val fragment: Fragment,
    private val activityCallbacks: ActivityCallbacks,
    private val stack: String,
    private val onClick: (String)->Unit
): RecyclerView.Adapter<PhotoPreviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoPreviewHolder {
        val binding = PhotoPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = PhotoPreviewHolder(binding, context, fragment, activityCallbacks, stack)
        binding.photo.setOnClickListener {
            val position = myHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClick(items[position].imageUrl)
            }
        }
        return myHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PhotoPreviewHolder, position: Int) {
        holder.bind(items[position])
    }
}
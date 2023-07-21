package com.skyyaros.skillcinema.ui.photography

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.PhotoPreviewTwoBigBinding
import com.skyyaros.skillcinema.databinding.PhotoPreviewTwoSmallBinding
import com.skyyaros.skillcinema.entity.ImageItem

class PhotoPreviewTwoAdapter(
    private val context: Context,
    private val orientation: Int,
    private val onClick:(String)->Unit
): PagingDataAdapter<ImageItem, RecyclerView.ViewHolder>(DiffUtilCallback()) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            if (position % 3 == 2 || orientation == Configuration.ORIENTATION_LANDSCAPE)
                (holder as PhotoPreviewTwoBigHolder).bind(item)
            else
                (holder as PhotoPreviewTwoSmallHolder).bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ONE && orientation == Configuration.ORIENTATION_PORTRAIT) {
            val binding = PhotoPreviewTwoSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val myHolder = PhotoPreviewTwoSmallHolder(binding, context)
            binding.root.setOnClickListener {
                val position = myHolder.bindingAdapterPosition
                val item = getItem(position)
                if (item != null) {
                    onClick(item.imageUrl)
                }
            }
            myHolder
        }
        else {
            val binding = PhotoPreviewTwoBigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val myHolder = PhotoPreviewTwoBigHolder(binding, context)
            binding.root.setOnClickListener {
                val position = myHolder.bindingAdapterPosition
                val item = getItem(position)
                if (item != null) {
                    onClick(item.imageUrl)
                }
            }
            myHolder
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 3 == 2) VIEW_TYPE_TWO else VIEW_TYPE_ONE
    }

    private companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }
}

class DiffUtilCallback: DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.previewUrl == newItem.previewUrl
    }
    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean = oldItem == newItem
}
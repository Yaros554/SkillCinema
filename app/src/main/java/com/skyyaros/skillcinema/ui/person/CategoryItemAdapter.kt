package com.skyyaros.skillcinema.ui.person

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.CategoryItemBinding
import com.skyyaros.skillcinema.entity.FilmActorTable

class CategoryItemAdapter(
    private val context: Context,
    private val onClick: (List<FilmActorTable>)->Unit,
    private val onDelete: (String)->Unit
): ListAdapter<List<FilmActorTable>, CategoryItemHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = CategoryItemHolder(binding, context)
        binding.root.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onClick(getItem(pos))
            }
        }
        binding.delete.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onDelete(getItem(pos)[0].category)
            }
        }
        return myHolder
    }

    override fun onBindViewHolder(holder: CategoryItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback: DiffUtil.ItemCallback<List<FilmActorTable>>() {
        override fun areItemsTheSame(oldItem: List<FilmActorTable>, newItem: List<FilmActorTable>): Boolean {
            return oldItem[0].category == newItem[0].category
        }

        override fun areContentsTheSame(oldItem: List<FilmActorTable>, newItem: List<FilmActorTable>): Boolean {
            return oldItem == newItem
        }
    }
}
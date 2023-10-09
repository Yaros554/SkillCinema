package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.CategoryItemForFilmBinding
import com.skyyaros.skillcinema.entity.FilmActorTable

class CategoryItemForFilmAdapter(
    private val context: Context,
    private val filmId: Long,
    private val onClick: (String, Boolean)->Boolean
): ListAdapter<List<FilmActorTable>, CategoryItemForFilmHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemForFilmHolder {
        val binding = CategoryItemForFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = CategoryItemForFilmHolder(binding, context, filmId)
        binding.root.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.checkBox.isChecked = !binding.checkBox.isChecked
                val res = onClick(getItem(pos)[0].category,  binding.checkBox.isChecked)
                if (res)
                    binding.checkBox.isChecked = false
            }
        }
        binding.checkBox.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val res = onClick(getItem(pos)[0].category,  binding.checkBox.isChecked)
                if (res)
                    binding.checkBox.isChecked = false
            }
        }
        return myHolder
    }

    override fun onBindViewHolder(holder: CategoryItemForFilmHolder, position: Int) {
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
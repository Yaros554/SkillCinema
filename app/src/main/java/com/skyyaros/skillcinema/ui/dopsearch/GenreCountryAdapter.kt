package com.skyyaros.skillcinema.ui.dopsearch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.skyyaros.skillcinema.databinding.SearchGenreCountryElBinding

class GenreCountryAdapter(
    private val selectItem: Long,
    private val context: Context,
    private val onClick: (Long)->Unit
): ListAdapter<GenreOrCountry, GenreCountryHolder>(DiffUtilCallback()) {
    override fun onBindViewHolder(holder: GenreCountryHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item.text, selectItem == item.id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreCountryHolder {
        val binding = SearchGenreCountryElBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = GenreCountryHolder(binding, context)
        binding.root.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            val item = getItem(pos)
            if (item != null)
                onClick(item.id)
        }
        return myHolder
    }

    class DiffUtilCallback: DiffUtil.ItemCallback<GenreOrCountry>() {
        override fun areItemsTheSame(oldItem: GenreOrCountry, newItem: GenreOrCountry): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GenreOrCountry, newItem: GenreOrCountry): Boolean {
            return oldItem == newItem
        }

    }
}
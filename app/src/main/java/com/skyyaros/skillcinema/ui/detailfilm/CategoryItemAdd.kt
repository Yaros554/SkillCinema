package com.skyyaros.skillcinema.ui.detailfilm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.CategoryItemAddBinding

class CategoryItemAddAdapter(private val onClick: ()->Unit): RecyclerView.Adapter<CategoryItemAddHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemAddHolder {
        val binding = CategoryItemAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = CategoryItemAddHolder(binding)
        binding.root.setOnClickListener {
            val pos = myHolder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION)
                onClick()
        }
        return myHolder
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: CategoryItemAddHolder, position: Int) {}
}

class CategoryItemAddHolder(binding: CategoryItemAddBinding): RecyclerView.ViewHolder(binding.root)
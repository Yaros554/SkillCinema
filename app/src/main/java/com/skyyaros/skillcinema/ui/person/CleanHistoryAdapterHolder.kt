package com.skyyaros.skillcinema.ui.person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.CleanHistoryBinding

class CleanHistoryHolder(binding: CleanHistoryBinding): RecyclerView.ViewHolder(binding.root)

class CleanHistoryAdapter(
    private val mode: String, private val onClick: (String)->Unit
): RecyclerView.Adapter<CleanHistoryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CleanHistoryHolder {
        val binding = CleanHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = CleanHistoryHolder(binding)
        binding.imageButton.setOnClickListener {
            onClick(mode)
        }
        return myHolder
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: CleanHistoryHolder, position: Int) {}
}
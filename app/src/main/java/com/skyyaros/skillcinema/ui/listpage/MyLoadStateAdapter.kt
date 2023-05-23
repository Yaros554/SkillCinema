package com.skyyaros.skillcinema.ui.listpage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.LoadStateBinding

class LoadStateViewHolder(private val binding: LoadStateBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.imageButton.setOnClickListener {
            retry()
        }
    }

    fun bind(loadState: LoadState) {
        binding.progressBar.visibility = toVisibility(loadState is LoadState.Loading)
        binding.imageButton.visibility = toVisibility(loadState !is LoadState.Loading)
    }

    private fun toVisibility(constraint: Boolean): Int = if (constraint) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

class MyLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(LoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false), retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}
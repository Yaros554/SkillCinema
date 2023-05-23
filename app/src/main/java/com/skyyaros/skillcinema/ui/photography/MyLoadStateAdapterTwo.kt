package com.skyyaros.skillcinema.ui.photography

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.LoadStateTwoBinding

class LoadStateViewHolderTwo(private val binding: LoadStateTwoBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {
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

class MyLoadStateAdapterTwo(private val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolderTwo>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolderTwo {
        return LoadStateViewHolderTwo(LoadStateTwoBinding.inflate(LayoutInflater.from(parent.context), parent, false), retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolderTwo, loadState: LoadState) {
        holder.bind(loadState)
    }
}
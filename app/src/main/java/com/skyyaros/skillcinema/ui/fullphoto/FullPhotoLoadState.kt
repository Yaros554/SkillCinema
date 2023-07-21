package com.skyyaros.skillcinema.ui.fullphoto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FullPhotoLoadStateBinding

class FullPhotoLoadStateHolder(
    private val binding: FullPhotoLoadStateBinding,
    private val retry: ()->Unit,
    private val onClick: ()->Unit
): RecyclerView.ViewHolder(binding.root) {
    fun bind(loadState: LoadState) {
        binding.root.setOnClickListener { onClick() }
        binding.btnError.setOnClickListener { retry() }
        if (loadState is LoadState.Loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.errorText.visibility = View.GONE
            binding.btnError.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.errorText.visibility = View.VISIBLE
            binding.btnError.visibility = View.VISIBLE
        }
    }
}

class FullPhotoLoadStateAdapter(private val retry: ()->Unit, private val onClick: () -> Unit): LoadStateAdapter<FullPhotoLoadStateHolder>() {
    override fun onBindViewHolder(holder: FullPhotoLoadStateHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FullPhotoLoadStateHolder {
        return FullPhotoLoadStateHolder(
            FullPhotoLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retry,
            onClick
        )
    }
}
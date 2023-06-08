package com.skyyaros.skillcinema.ui.hello

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.HelloFragmentItemBinding

class ViewPagerHolder(private val binding: HelloFragmentItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HelloFragment.HelloFragmentData) {
        binding.imageView.setImageResource(item.imageRes)
        binding.textView.text = item.text
    }
}
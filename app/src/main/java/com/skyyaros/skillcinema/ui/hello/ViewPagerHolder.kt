package com.skyyaros.skillcinema.ui.hello

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.HelloFragmentItemBinding

class ViewPagerHolder(private val binding: HelloFragmentItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HelloFragment.HelloFragmentData, context: Context, onButtonClicked: () -> Unit) {
        val resId = context.resources.getIdentifier(item.image, "drawable", context.packageName)
        binding.imageView.setImageResource(resId)
        binding.textView.text = item.text
        val resId2 = context.resources.getIdentifier(item.imagePoints, "drawable", context.packageName)
        binding.imagePoints.setImageResource(resId2)
        binding.skip.text = if (item.isFinish) context.getString(R.string.hello_start) else context.getString(R.string.hello_skip)
        binding.skip.setOnClickListener { onButtonClicked() }
    }
}
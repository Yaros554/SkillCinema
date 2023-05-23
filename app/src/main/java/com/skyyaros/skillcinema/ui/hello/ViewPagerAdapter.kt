package com.skyyaros.skillcinema.ui.hello

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.HelloFragmentItemBinding

class ViewPagerAdapter(
    private val items: List<HelloFragment.HelloFragmentData>,
    private val context: Context,
    private val onButtonClicked: () -> Unit
): RecyclerView.Adapter<ViewPagerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        return ViewPagerHolder(HelloFragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val item = items[position]
        holder.bind(item, context, onButtonClicked)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
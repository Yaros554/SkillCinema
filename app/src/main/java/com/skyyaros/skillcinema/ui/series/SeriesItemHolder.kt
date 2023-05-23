package com.skyyaros.skillcinema.ui.series

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.SeriesItemViewPagerBinding
import com.skyyaros.skillcinema.entity.Season
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import java.util.Locale

class SeriesItemHolder(private val binding: SeriesItemViewPagerBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Season) {
        binding.count.text = if (Locale.getDefault().language == "ru") {
            val size = item.episodes.size
            if (size in 11..19)
                "${item.number} сезон, $size серий"
            else if (size % 10 == 1)
                "${item.number} сезон, $size серия"
            else if (size % 10 in 2..4)
                "${item.number} сезон, $size серии"
            else
                "${item.number} сезон, $size серий"
        } else {
            "${item.number} season, ${item.episodes.size} episodes"
        }
        val adapter = SeriesPreviewAdapter(item.episodes, context)
        binding.recycler.adapter = adapter
        val itemMargin = AdaptiveSpacingItemDecoration(context.resources.getDimension(R.dimen.big_margin).toInt(), false)
        if (binding.recycler.itemDecorationCount == 0) {
            binding.recycler.addItemDecoration(itemMargin)
        }
    }
}
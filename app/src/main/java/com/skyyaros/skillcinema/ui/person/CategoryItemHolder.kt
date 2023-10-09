package com.skyyaros.skillcinema.ui.person

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.CategoryItemBinding
import com.skyyaros.skillcinema.entity.FilmActorTable

class CategoryItemHolder(private val binding: CategoryItemBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: List<FilmActorTable>) {
        when(item[0].category) {
            "2" -> {
                binding.imageView.setImageDrawable(context.getDrawable(R.drawable.share_for_profile))
                binding.name.text = context.getString(R.string.profile_item_text2)
                binding.delete.visibility = View.GONE
            }
            "3" -> {
                binding.imageView.setImageDrawable(context.getDrawable(R.drawable.like_for_profile))
                binding.name.text = context.getString(R.string.profile_item_text)
                binding.delete.visibility = View.GONE
            }
            else -> {
                binding.imageView.setImageDrawable(context.getDrawable(R.drawable.user_for_profile))
                binding.name.text = item[0].category
                binding.delete.visibility = View.VISIBLE
            }
        }
        binding.count.text = (item.filter { it.kinopoiskId != -1L }.size).toString()
    }
}
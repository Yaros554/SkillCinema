package com.skyyaros.skillcinema.ui.fullphoto

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.skyyaros.skillcinema.databinding.FullPhotoFrAndVpBinding
import com.skyyaros.skillcinema.entity.ImageItem

class FullPhotoAdapterNoPaging(
    private val items: List<ImageItem>,
    private val context: Context,
    private val fragment: Fragment,
    private val onClick: ()->Unit
): RecyclerView.Adapter<FullPhotoHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullPhotoHolder {
        val binding = FullPhotoFrAndVpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = FullPhotoHolder(binding, context, fragment)
        binding.imageView.setOnClickListener { onClick() }
        binding.imageView.setOnTouchListener { view, event ->
            var result = true
            //can scroll horizontally checks if there's still a part of the image
            //that can be scrolled until you reach the edge
            if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && view.canScrollHorizontally(-1)) {
                //multi-touch event
                result = when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        // Disallow RecyclerView to intercept touch events.
                        parent.requestDisallowInterceptTouchEvent(true)
                        // Disable touch on view
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        // Allow RecyclerView to intercept touch events.
                        parent.requestDisallowInterceptTouchEvent(false)
                        true
                    }
                    else -> true
                }
            }
            result
        }
        return myHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FullPhotoHolder, position: Int) {
        holder.bind(items[position])
    }
}
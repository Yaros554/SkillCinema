package com.skyyaros.skillcinema.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class LeftSpaceDecorator(private val space: Int): ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                with(outRect) {
                    left = if (position / layoutManager.spanCount == 0) space else 0
                    right = 0
                    top = 0
                    bottom = 0
                }
            }
            is LinearLayoutManager -> {
                with(outRect) {
                    left = if (position == 0) space else 0
                    right = 0
                    top = 0
                    bottom = 0
                }
            }
            is StaggeredGridLayoutManager -> {
                with(outRect) {
                    left = if (position / layoutManager.spanCount == 0) space else 0
                    right = 0
                    top = 0
                    bottom = 0
                }
            }
        }
    }
}
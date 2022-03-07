package com.bcsd.android.lotteryticketapplication.view.homeFragment

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeScreenViewItemDecoration() : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val count = state.itemCount
        val offset = 20

        outRect.bottom = offset

    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val paint = Paint()
        paint.color = Color.GRAY

        val left = parent.paddingStart.toFloat()
        val right = (parent.width - parent.paddingEnd).toFloat()

        for(i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = (child.bottom + layoutParams.bottomMargin + 20).toFloat()
            val bottom = top + 1f

            c.drawRect(left, top, right, bottom, paint)
        }
    }

}
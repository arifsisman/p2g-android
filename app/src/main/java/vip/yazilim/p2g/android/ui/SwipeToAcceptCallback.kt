package vip.yazilim.p2g.android.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import vip.yazilim.p2g.android.R

/**
 * @author mustafaarifsisman - 19.02.2020
 * @contact mustafaarifsisman@gmail.com
 */

abstract class SwipeToAcceptCallback(context: Context?) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val acceptIcon: Drawable =
        context?.let { ContextCompat.getDrawable(it, R.drawable.ic_check_white_24dp) }!!
    private val intrinsicWidth = acceptIcon.intrinsicWidth
    private val intrinsicHeight = acceptIcon.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#1DB954")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.left + dX,
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the green accept background
        background.color = backgroundColor
        background.setBounds(
            itemView.left,
            itemView.top,
            itemView.left + dX.toInt(),
            itemView.bottom
        )
        background.draw(c)

        // Calculate position of accept icon
        val acceptIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val acceptIconMargin = (itemHeight - intrinsicHeight) / 2
        val acceptIconLeft = itemView.left - acceptIconMargin - intrinsicWidth
        val acceptIconRight = itemView.left - acceptIconMargin
        val acceptIconBottom = acceptIconTop + intrinsicHeight

        // Draw the accept icon
        acceptIcon.setBounds(acceptIconLeft, acceptIconTop, acceptIconRight, acceptIconBottom)
        acceptIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
package vip.yazilim.p2g.android.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @author mustafaarifsisman - 19.02.2020
 * @contact mustafaarifsisman@gmail.com
 */

abstract class SwipeToAcceptCallback(
    private val acceptIcon: Drawable,
    private val backgroundColor: Int
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val intrinsicWidth = acceptIcon.intrinsicWidth
    private val intrinsicHeight = acceptIcon.intrinsicHeight
    private val background = ColorDrawable()

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
        val acceptIconLeft = -(itemView.left - acceptIconMargin)
        val acceptIconRight = -(itemView.left - acceptIconMargin - intrinsicWidth)
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
package vip.yazilim.p2g.android.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolderBase<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(item: T)
    }
package com.example.reelers.StaggeredLayout

import android.content.Context
import android.os.Handler
import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.Math.min
import kotlin.math.max

class StaggeredLayout : StaggeredGridLayoutManager {

    companion object {
        private fun setInitialSpanCount(context: Context?, columnWidthDp: Int) : Int {
            val displayMetrics: DisplayMetrics? = context?.resources?.displayMetrics
            return ((displayMetrics?.widthPixels ?: columnWidthDp) / columnWidthDp)
        }
    }

    private var mContext: Context?
    private var mColumnWidth = 0
    private var mMaximumColumns: Int
    private var mLastCalculatedWidth = -1

    @JvmOverloads
    constructor(
        context: Context?,
        columnWidthDp: Int,
        maxColumns: Int = 99
    ) : super(setInitialSpanCount(context, columnWidthDp), VERTICAL)
    {
        mContext = context
        mMaximumColumns = maxColumns
        mColumnWidth = columnWidthDp
    }

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        if (width != mLastCalculatedWidth && width > 0) {
            recalculateSpanCount()
        }
        super.onLayoutChildren(recycler, state)
    }

    private fun recalculateSpanCount() {
        val totalSpace: Int = if (orientation == RecyclerView.VERTICAL) {
            width - paddingRight - paddingLeft
        } else {
            height - paddingTop - paddingBottom
        }
        val newSpanCount = min(
            mMaximumColumns,
            max(1, totalSpace / mColumnWidth)
        )
        queueSetSpanCountUpdate(newSpanCount)
        mLastCalculatedWidth = width
    }

    private fun queueSetSpanCountUpdate(newSpanCount: Int) {
        if(mContext != null) {
            Handler(mContext!!.mainLooper).post { spanCount = newSpanCount }
        }
    }
}
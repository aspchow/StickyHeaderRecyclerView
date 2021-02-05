package com.example.stickyheader

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.databinding.SectionHeaderBinding

class RecyclerViewItemDecoration(val context: Context, val headerOffSet: Int, val isSticky: Boolean, val sectionCallback: SectionCallBack) : RecyclerView.ItemDecoration() {

    lateinit var headerBinding: SectionHeaderBinding

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (sectionCallback.isSectionHeader(position)) {
            outRect.top = dpToPx(30).toInt()
        }
    }
    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (::headerBinding.isInitialized.not()) {
            headerBinding = inflateHeader(parent)
            fixLayoutSize(headerBinding.root, parent)
        }
        var prevTitle = ""
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val childPos = parent.getChildAdapterPosition(child)
            val title = sectionCallback.getSectionHeaderName(childPos)
            headerBinding.headerText.text = title
            if (prevTitle != title || sectionCallback.isSectionHeader(childPos)) {
                if (sectionCallback.isSectionHeader(childPos + 1))
                {
                    val nextChild = parent.getChildAt(i+1)
                    drawHeader(c, child, headerBinding.root, true,nextChild)
                }
                else
                    drawHeader(c, child, headerBinding.root)
                prevTitle = title
            }
        }
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View, isNextItemStickyHeader: Boolean= false, nextChild: View? = null) {
        c.save()
        if (isSticky) {
            if (isNextItemStickyHeader) {
                c.translate(0F, Math.min(0, nextChild!!.top - 2 * headerView.height).toFloat())
            } else {
                c.translate(0F, Math.max(0, child.top - headerView.height).toFloat())
            }
        } else {
            c.translate(0F, child.top - headerView.height.toFloat())
        }
        headerView.draw(c)
        c.restore()
    }

    private fun fixLayoutSize(headerView: View, parent: ViewGroup) {

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, headerView.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, headerView.layoutParams.height)

        headerView.measure(childWidth, childHeight)
        headerView.layout(0, 0, headerView.measuredWidth, headerView.measuredHeight)
    }

    private fun inflateHeader(recyclerView: RecyclerView): SectionHeaderBinding {
        return SectionHeaderBinding.inflate(LayoutInflater.from(context), recyclerView, false)
    }
}

interface SectionCallBack {
    fun isSectionHeader(position: Int): Boolean
    fun getSectionHeaderName(position: Int): String
}

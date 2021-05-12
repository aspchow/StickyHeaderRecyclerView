package com.example.stickyheader

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.stickyheader.databinding.SectionHeaderBinding

class RecyclerViewItemDecoration(
    private val isSticky: Boolean,
    private val sectionCallback: SectionCallBack
) : RecyclerView.ItemDecoration() {

    lateinit var headerBinding: SectionHeaderBinding

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (sectionCallback.isSectionHeader(position)) {
            if (::headerBinding.isInitialized.not()) {
                headerBinding = inflateHeader(parent)
                fixLayoutSize(headerBinding.root, parent)
            }
            outRect.top =
                headerBinding.root.height
        }
    }



    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
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
                if (i == 0 ) {
                    var nextChild: View? = null
                    for (j in i until parent.childCount) {
                        if (title != sectionCallback.getSectionHeaderName(childPos + j)) {
                            nextChild = parent.getChildAt(j)
                            break
                        }
                    }

                    //nextChild = parent.getChildAt(i + 1)
                    if(nextChild != null){
                        drawHeader(c, child, true, nextChild)
                    }
                    else{
                        drawHeader(c, child)
                    }

                } else
                    drawHeader(c, child)
                prevTitle = title
            }
        }
    }


    private fun drawHeader(
        c: Canvas,
        child: View,
        isNextItemStickyHeader: Boolean = false,
        nextChild: View? = null
    ) {
        c.save()
        if (isSticky) {
            if (isNextItemStickyHeader) {
                printMsg("the height of child top -> ${nextChild!!.top} , header Height ${headerBinding.root.height} child height is ${child.height}")
                c.translate(
                    0F,
                    minOf(
                        0F, nextChild.top - 2 * (headerBinding.root.height).toFloat()
                    )

                )
            } else {
                c.translate(
                    0F,
                    maxOf(0f, child.top - headerBinding.root.height.toFloat())
                )
            }
        } else {
            c.translate(0F, child.top - headerBinding.root.height.toFloat())
        }

        headerBinding.root.draw(c)
        c.restore()
    }

    private fun fixLayoutSize(headerView: View, parent: ViewGroup) {

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            headerView.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            headerView.layoutParams.height
        )

        headerView.measure(childWidth, childHeight)
        headerView.layout(0, 0, headerView.measuredWidth, headerView.measuredHeight)
    }

    private fun inflateHeader(recyclerView: RecyclerView): SectionHeaderBinding {
        return SectionHeaderBinding.inflate(
            LayoutInflater.from(recyclerView.context),
            recyclerView,
            false
        )
    }
}

interface SectionCallBack {
    fun isSectionHeader(position: Int): Boolean
    fun getSectionHeaderName(position: Int): String
}

// trying to experiment on this of different sticky headers
interface SectionCallBack2<T : ViewBinding>{
    fun getSectionHeaderName(position: Int , binding : T): T
}
package com.jm.voicerec.recyclerview

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jm.voicerec.R
import com.jm.voicerec.SelectItemInterface
import kotlin.math.pow

class HorizontalCarouselRecyclerView(context: Context, attrs : AttributeSet) : RecyclerView(context, attrs){
    private val activeColor by lazy { ContextCompat.getColor(context, R.color.skyBlue) }
    private val inactiveColor by lazy { ContextCompat.getColor(context, R.color.gray) }
    private var viewsToChangeColor: List<Int> = listOf()

    fun <T : ViewHolder> initialize(newAdapter: Adapter<T>) {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)

        newAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                post {
                    val sidePadding = (width / 2) - (getChildAt(0).width / 2)
                    setPadding(sidePadding, 0, sidePadding, 0)
                    smoothScrollToPosition(0)

                    // test visible position listener
//                    addOnScrollListener(object : VisiblePositionChangeListner(layoutManager as LinearLayoutManager, object : OnChangeListner{
//                        override fun onFirstVisiblePositionChanged(position: Int) {
//                            Log.e("listner>>","F $position")
//                            (context as SelectItemInterface).currentItem(position)
//                        }
//
//                        override fun onLastVisiblePositionChanged(position: Int) {
//                            Log.e("listner>>","L $position")
//                            (context as SelectItemInterface).currentItem(position)
//                        }
//                    }
//                    ){
//                        override fun onScrollStateChanged(
//                            recyclerView: RecyclerView,
//                            newState: Int,
//                        ) {
//                            super.onScrollStateChanged(recyclerView, newState)
//                            (context as SelectItemInterface).stateSelected(newState)
//                        }
//
//                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                            super.onScrolled(recyclerView, dx, dy)
//                            onScrollChanged()
//                        }
//                    })

                    addOnScrollListener(object : OnScrollListener() {

                        var c2 = false
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int
                        ) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if(newState==0)
                                (context as SelectItemInterface).stateSelected(c2)
                            c2 = false
                        }

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            c2 = true
                            onScrollChanged()
                        }
                    })
                }
            }
        })
        adapter = newAdapter
    }

    fun setViewsToChangeColor(viewIds: List<Int>) {
        viewsToChangeColor = viewIds
    }

    private fun onScrollChanged() {
        post {
            (0 until childCount).forEach { position ->
                val child = getChildAt(position)
                val childCenterX = (child.left + child.right) / 2
                val scaleValue = getGaussianScale(childCenterX, 1f, 1f, 150.toDouble())

                // change scale
                child.scaleX = scaleValue/1.5f
                child.scaleY = scaleValue/1.5f
                // change color
                colorView(child, scaleValue)
            }
        }
    }

    private fun colorView(child: View, scaleValue: Float) {
        val saturationPercent = (scaleValue - 1) / 1f
        val alphaPercent = scaleValue / 2f
        val matrix = ColorMatrix()
        matrix.setSaturation(saturationPercent)

        viewsToChangeColor.forEach { viewId ->
            when (val viewToChangeColor = child.findViewById<View>(viewId)) {
                is TextView -> {
                    val textColor = ArgbEvaluator().evaluate(saturationPercent, inactiveColor, activeColor) as Int
                    viewToChangeColor.setTextColor(textColor)
                }
                is ImageView -> {
                    viewToChangeColor.colorFilter = ColorMatrixColorFilter(matrix)
                    viewToChangeColor.imageAlpha = (255 * alphaPercent).toInt()
                }
            }
        }
    }

    private fun getGaussianScale(
        childCenterX: Int,
        minScaleOffest: Float,
        scaleFactor: Float,
        spreadFactor: Double
    ): Float {
        val recyclerCenterX = (left + right) / 2
        return (Math.E.pow(
            -(childCenterX - recyclerCenterX.toDouble()).pow(2.toDouble())
                    / (2 * spreadFactor.pow(2.toDouble()))
        ) * scaleFactor + minScaleOffest).toFloat()
    }
}
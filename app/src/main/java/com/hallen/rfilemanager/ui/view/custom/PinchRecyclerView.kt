package com.hallen.rfilemanager.ui.view.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.model.LayoutManagerType
import kotlin.math.max
import kotlin.math.min

class PinchRecyclerView : RecyclerView {
    private var mActivePointerId = INVALID_POINTER_ID
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1f
    private var maxWidth = 0.0f
    private var maxHeight = 0.0f
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mPosX = 0f
    private var mPosY = 0f
    private var width = 0f
    private var height = 0f

    constructor(context: Context?) : super(context!!) {
        if (!isInEditMode) mScaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        if (!isInEditMode) mScaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        if (!isInEditMode) mScaleDetector = ScaleGestureDetector(getContext(), ScaleListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        super.onTouchEvent(ev)
        val action = ev.action
        mScaleDetector!!.onTouchEvent(ev)
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                mLastTouchX = x
                mLastTouchY = y
                mActivePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = (action and MotionEvent.ACTION_POINTER_INDEX_MASK
                        shr MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY
                mPosX += dx
                mPosY += dy
                if (mPosX > 0.0f) mPosX = 0.0f else if (mPosX < maxWidth) mPosX = maxWidth
                if (mPosY > 0.0f) mPosY = 0.0f else if (mPosY < maxHeight) mPosY = maxHeight
                mLastTouchX = x
                mLastTouchY = y
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex =
                    action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(mPosX, mPosY)
        //canvas.scale(mScaleFactor, mScaleFactor)
        canvas.restore()
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        if (mScaleFactor == 1.0f) {
            mPosX = 0.0f
            mPosY = 0.0f
        }
        canvas.translate(mPosX, mPosY)
        //canvas.scale(mScaleFactor, mScaleFactor)
        super.dispatchDraw(canvas)
        canvas.restore()
        invalidate()
    }

    public interface OnScaleListener {
        fun OnScale(scale: Int)
    }

    private var scaleListener: OnScaleListener? = null

    fun setOnScaleListener(scaleListener: OnScaleListener) {
        this.scaleListener = scaleListener
    }

    fun setOnScaleListener(scaleListener: (Int) -> Unit) {
        setOnScaleListener(object : OnScaleListener {
            override fun OnScale(scale: Int) {
                scaleListener(scale)
            }
        })
    }


    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = max(1.0f, min(mScaleFactor, 3.0f))
            val scale = ((mScaleFactor * 2).toInt() - 6) * -1 + 2
            val layoutManager = this@PinchRecyclerView.layoutManager
            if (layoutManager is GridLayoutManager) {
                layoutManager.spanCount = scale
            }
            invalidate()
            scaleListener?.OnScale(scale)
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            this@PinchRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        val GRID_MODE: String = "grid"
        val LINEAR_MODE: String = "linear"
        private const val INVALID_POINTER_ID = -1
    }
}

fun RecyclerView.setLayoutManager(type: LayoutManagerType, scale: Int) {
    val firstCompletelyVisibleItemPosition =
        (layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
    layoutManager = if (type == LayoutManagerType.GRID_LAYOUT_MANAGER)
        GridLayoutManager(context, scale) else LinearLayoutManager(context)
    firstCompletelyVisibleItemPosition?.let { scrollToPosition(it) }
}
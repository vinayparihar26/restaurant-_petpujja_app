package com.example.restaurant.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class HeartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var isHeartSelected = false
    private val heartPath = Path()
    private val paint = Paint().apply {
        color = Color.WHITE // Default color
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createHeartPath(w, h)
    }

    private fun createHeartPath(width: Int, height: Int) {
        val centerX = width / 2f
        val centerY = height / 2f
        val size = width.coerceAtMost(height) / 2f

        heartPath.reset()
        heartPath.moveTo(centerX, centerY + size / 4)
        heartPath.cubicTo(
            centerX - size,
            centerY - size / 2,
            centerX - size,
            centerY + size,
            centerX,
            centerY + size * 1.2f
        )
        heartPath.cubicTo(
            centerX + size,
            centerY + size,
            centerX + size,
            centerY - size / 2,
            centerX,
            centerY + size / 4
        )
        heartPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(heartPath, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            isHeartSelected = !isHeartSelected
            animateHeartColor()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun animateHeartColor() {
        val colorStart = if (isHeartSelected) Color.WHITE else Color.RED
        val colorEnd = if (isHeartSelected) Color.RED else Color.WHITE

        ValueAnimator.ofArgb(colorStart, colorEnd).apply {
            duration = 500
            addUpdateListener {
                paint.color = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }
}

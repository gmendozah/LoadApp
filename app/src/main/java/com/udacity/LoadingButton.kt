package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var customBackgroundColor: Int = 0
    private var customTextColor: Int = 0
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        textSize = 35f
        textAlign = Paint.Align.CENTER
    }


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        invalidate()
    }


    init {
        context.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            try {
                customBackgroundColor = getColor(R.styleable.LoadingButton_customBackgroundColor, 0)
                customTextColor = getColor(R.styleable.LoadingButton_customTextColor, 0)
            } finally {
                recycle()
            }
        }
    }

    @JvmName("setButtonState1")
    fun setButtonState(state: ButtonState) {
        buttonState = state
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            // draw background
            drawColor(customBackgroundColor)

            paint.color = customTextColor

            // draw circle
            // draw text
            if(buttonState == ButtonState.Loading) {
                canvas.drawText(context.getString(R.string.loading), widthSize / 2f, heightSize / 2f - (paint.descent() + paint.ascent()) / 2, paint)
            } else {
                canvas.drawText(context.getString(R.string.download), widthSize / 2f, heightSize / 2f - (paint.descent() + paint.ascent()) / 2, paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}
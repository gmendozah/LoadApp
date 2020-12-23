package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.properties.Delegates

@SuppressLint("Recycle")
class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var customBackgroundColor: Int = 0
    private var customTextColor: Int = 0
    private var widthSize = 0
    private var heightSize = 0
    private var text = ""

    // this value gets till 360 degrees
    private var circularProgress: Float = 0F

    // this value goes from 0 to 1
    private var linearProgress: Float = 0F
    private var radius: Float = 0F

    private val animationDuration = 2000L
    private val valueAnimator = ValueAnimator()

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        textSize = 35f
        textAlign = Paint.Align.CENTER
    }


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Loading) {
            valueAnimator.cancel()
            valueAnimator.apply {
                duration = animationDuration
                setIntValues(0, 360)
                interpolator = LinearInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Int
                    circularProgress = value.toFloat()
                    linearProgress = (value / 360F)
                    invalidate()
                }
            }
            valueAnimator.start()
        }
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

    @JvmName("getButtonState1")
    fun getButtonState(): ButtonState = buttonState


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            // set background
            drawColor(customBackgroundColor)
            if (buttonState == ButtonState.Loading) {
                // draw rect
                val rect = Rect(0, 0, (widthSize * linearProgress).toInt(), heightSize)
                paint.color = context.getColor(R.color.colorPrimaryDark)
                canvas.drawRect(rect, paint)
                // draw circle
                paint.color = context.getColor(R.color.colorAccent)
                text = context.getString(R.string.loading)
                radius = heightSize * 0.7F
                val posX = widthSize - (radius + 40F)
                val posY = heightSize / 2 - (radius / 2)
                val oval = RectF(posX, posY, posX + radius, posY + radius)
                canvas.drawArc(
                        oval,
                        0f,
                        circularProgress,
                        true,
                        paint
                )
            } else {
                text = context.getString(R.string.download)
            }
            // draw text
            paint.color = customTextColor
            canvas.drawText(text, widthSize / 2f, heightSize / 2f - (paint.descent() + paint.ascent()) / 2, paint)
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
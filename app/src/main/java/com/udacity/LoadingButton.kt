package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

/**
 * Class to implement custom behaviors of Loading Button
 */
class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var animateWidth = 0f
    private val valueAnimator = ValueAnimator()
    private var text = resources.getString(R.string.download)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                text = resources.getString(R.string.button_loading)
                loadingAnimation(true)
            }

            ButtonState.Loading -> {
                text = resources.getString(R.string.button_loading)
                loadingAnimation(false)
            }
            ButtonState.Completed -> {
                if (valueAnimator.isRunning) {
                    valueAnimator.removeAllUpdateListeners()
                    valueAnimator.end()
                }
                text = resources.getString(R.string.download)
                animateWidth = 0f
                invalidate()
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorPrimary)
    }

    private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorPrimaryDark)
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorAccent)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.NORMAL)
        color = Color.WHITE
    }

    init {
    }

    private fun loadingAnimation(noUrl: Boolean) {
        valueAnimator.apply {
            setFloatValues(0f, widthSize.toFloat())
            duration = 10000
            repeatCount = 0
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                animateWidth = animatedValue as Float
                this@LoadingButton.invalidate()
                if (noUrl && animateWidth == widthSize.toFloat()) buttonState = ButtonState.Completed
            }

            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = RectF(0f, 0f, widthSize.toFloat(), heightSize.toFloat())
        val rectDark = RectF(0f, 0f, animateWidth, heightSize.toFloat())
        val rectCircle = RectF(widthSize.toFloat() / 1.4f, heightSize.toFloat() / 2 - 30, widthSize.toFloat() / 1.4f + 60, heightSize.toFloat() / 2 + 30)
        canvas.drawRect(rect, paint)
        canvas.drawRect(rectDark, darkPaint)
        canvas.drawArc(rectCircle, 0f, (animateWidth / widthSize) * 360, true, circlePaint)
        canvas.drawText(text, widthSize.toFloat() / 2, heightSize.toFloat() / 2 + 15, textPaint)
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

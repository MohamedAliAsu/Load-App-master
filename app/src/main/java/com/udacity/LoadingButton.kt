package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0


    var bgColor = resources.getColor(R.color.colorPrimary,null)
    var arcColor = resources.getColor(R.color.colorAccent,null)
    var bgLoadingColor = resources.getColor(R.color.colorPrimaryDark,null)
    var defaultButtonPaint = Paint().apply {
        isAntiAlias = true
        color = bgColor
    }
    var loadingButtonPaint = Paint().apply {
        isAntiAlias = true
        color = bgLoadingColor
    }
    var textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textSize = resources.getDimension(R.dimen.default_text_size)
    }
    var arcPaint = Paint().apply {
        color = arcColor
    }
    var TW = 0f
    var CIRCLEOFFSET = (resources.getDimension(R.dimen.default_text_size)) / 2


    var buttonText = ""

    var progress = 0f
    var cProgress = 0f

    val OvalDimens = RectF(0f,
        0f,
        (resources.getDimension(R.dimen.default_text_size)),
        (resources.getDimension(R.dimen.default_text_size)))
    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = "Loading"
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.apply {
                    addUpdateListener { anim ->
                        progress = anim.animatedValue as Float
                        cProgress = (widthSize.toFloat() / 365) * progress
                        invalidate()
                    }
                    duration = 2200
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            progress = 0f
                            cProgress = 0f
                            invalidate()
                            buttonState = ButtonState.Completed
                        }
                    })
                    start()
                }
            }
            ButtonState.Completed -> {
                isClickable = true
                valueAnimator.cancel()
                progress = 0f
                cProgress = 0f
                buttonText = "Start Downloading"
                invalidate()
            }
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
            }
        }
    }


    init {

        isClickable = true
        buttonState = ButtonState.Completed
        context.withStyledAttributes(attrs , R.styleable.LoadingButton){
            bgColor = getColor(R.styleable.LoadingButton_bgColor,0)
            bgLoadingColor = getColor(R.styleable.LoadingButton_bgLoadingColor,0)
            arcColor = getColor(R.styleable.LoadingButton_arcColor,0)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), defaultButtonPaint)
        canvas?.drawRect(0f, 0f, progress, heightSize.toFloat(), loadingButtonPaint)
        TW = textPaint.measureText(buttonText)
        canvas?.drawText(buttonText,
            widthSize / 2 - TW / 2,
            heightSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
            textPaint)
        canvas?.save()
        canvas?.translate(widthSize / 2 + TW / 2 + CIRCLEOFFSET,
            heightSize / 2 - (resources.getDimension(R.dimen.default_text_size) / 2))
        canvas?.drawArc(OvalDimens, 0f, cProgress * 0.360f, true, arcPaint)
        canvas?.restore()
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

    override fun performClick(): Boolean {
        super.performClick()
        isClickable = false
        buttonState = ButtonState.Loading
        return true
    }

}
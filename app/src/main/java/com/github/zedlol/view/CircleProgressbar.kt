package com.github.zedlol.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.github.zedlol.R


class CircleProgressbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    //外部轮廓的颜色
    private var outLineColor: Int = Color.BLACK

    //外部轮廓的宽度
    private var outLineWidth = 2

    //内部圆的颜色
    private var inCircleColors: ColorStateList = ColorStateList.valueOf(Color.TRANSPARENT)

    //中心圆的颜色
    private var circleColor = 0

    //进度条的颜色
    private var progressLineColor: Int = Color.BLUE

    //进度条的宽度
    private var progressLineWidth = 8

    //画笔
    private val mPaint: Paint = Paint()

    //进度条的矩形区域
    private val mArcRect: RectF = RectF()

    //进度
    private var progress = 100

    //进度条类型
    private var mProgressType: ProgressType = ProgressType.COUNT_BACK

    //进度倒计时时间
    private var timeMillis: Long = 3000

    //View的显示区域。
    val bounds: Rect = Rect()

    //进度条通知。
    private var mCountdownProgressListener: OnCountdownProgressListener? = null
    private var listenerWhat = 0

    init {
        mPaint.isAntiAlias = true
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressbar)
        if (typedArray.hasValue(R.styleable.CircleProgressbar_in_circle_color)) {
            typedArray.getColorStateList(R.styleable.CircleProgressbar_in_circle_color).let {
                inCircleColors = it ?: ColorStateList.valueOf(Color.TRANSPARENT)
            }
        }
        circleColor = inCircleColors.getColorForState(drawableState, Color.TRANSPARENT)
        typedArray.recycle()
    }

    fun setOutLineColor(@ColorInt outLineColor: Int) {
        this.outLineColor = outLineColor
        invalidate()
    }


    fun setOutLineWidth(@ColorInt outLineWidth: Int) {
        this.outLineWidth = outLineWidth
        invalidate()
    }


    fun setInCircleColor(@ColorInt inCircleColor: Int) {
        inCircleColors = ColorStateList.valueOf(inCircleColor)
        invalidate()
    }


    private fun validateCircleColor() {
        val circleColorTemp = inCircleColors.getColorForState(drawableState, Color.TRANSPARENT)
        if (circleColor != circleColorTemp) {
            circleColor = circleColorTemp
            invalidate()
        }
    }


    fun setProgressColor(@ColorInt progressLineColor: Int) {
        this.progressLineColor = progressLineColor
        invalidate()
    }


    fun setProgressLineWidth(progressLineWidth: Int) {
        this.progressLineWidth = progressLineWidth
        invalidate()
    }


    fun setProgress(progress: Int) {
        this.progress = validateProgress(progress)
        invalidate()
    }


    private fun validateProgress(progress: Int): Int {
        var pro = progress
        if (progress > 100) pro = 100 else if (progress < 0) pro = 0
        return pro
    }


    fun getProgress(): Int {
        return progress
    }


    fun setTimeMillis(timeMillis: Long) {
        this.timeMillis = timeMillis
        invalidate()
    }


    fun getTimeMillis(): Long {
        return timeMillis
    }


    fun setProgressType(progressType: ProgressType?) {
        mProgressType = progressType ?: ProgressType.COUNT_BACK
        resetProgress()
        invalidate()
    }


    private fun resetProgress() {
        when (mProgressType) {
            ProgressType.COUNT -> progress = 0
            ProgressType.COUNT_BACK -> progress = 100
        }
    }

    fun getProgressType(): ProgressType? {
        return mProgressType
    }


    fun setCountdownProgressListener(
        what: Int,
        mCountdownProgressListener: OnCountdownProgressListener?
    ) {
        listenerWhat = what
        this.mCountdownProgressListener = mCountdownProgressListener
    }

    fun start() {
        stop()
        post(progressChangeTask)
    }


    fun reStart() {
        resetProgress()
        start()
    }


    fun stop() {
        removeCallbacks(progressChangeTask)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val lineWidth = 4 * (outLineWidth + progressLineWidth)
        val width = measuredWidth
        val height = measuredHeight
        val size = (if (width > height) width else height) + lineWidth
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        //获取view的边界
        getDrawingRect(bounds)

        val size = if (bounds.height() > bounds.width()) bounds.width() else bounds.height()
        val outerRadius = (size / 2).toFloat()

        //画内部背景
        val circleColor = inCircleColors.getColorForState(drawableState, 0)
        mPaint.style = Paint.Style.FILL
        mPaint.color = circleColor
        canvas!!.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(), outerRadius - outLineWidth, mPaint
        )

        //画边框圆
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = outLineWidth.toFloat()
        mPaint.color = outLineColor
        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(), outerRadius - outLineWidth / 2, mPaint
        )

        //画字
        val paint: Paint = paint
        paint.color = currentTextColor
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        val textY = bounds.centerY() - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(text.toString(), bounds.centerX().toFloat(), textY, paint)

        //画进度条
        mPaint.color = progressLineColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = progressLineWidth.toFloat()
        mPaint.strokeCap = Paint.Cap.ROUND
        val deleteWidth = progressLineWidth + outLineWidth
        mArcRect[(bounds.left + deleteWidth / 2).toFloat(), (bounds.top + deleteWidth / 2).toFloat(), (bounds.right - deleteWidth / 2).toFloat()] =
            (bounds.bottom - deleteWidth / 2).toFloat()

        canvas.drawArc(mArcRect, 0.0f, (360 * progress / 100).toFloat(), false, mPaint)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        validateCircleColor()
    }

    private val progressChangeTask: Runnable = object : Runnable {
        override fun run() {
            removeCallbacks(this)
            when (mProgressType) {
                ProgressType.COUNT -> progress += 1
                ProgressType.COUNT_BACK -> progress -= 1
            }
            if (progress in 0..100) {
                mCountdownProgressListener?.onProgress(
                    listenerWhat,
                    progress
                )
                invalidate()
                postDelayed(this, timeMillis / 100)
            } else progress = validateProgress(progress)
        }
    }

    enum class ProgressType {
        /**
         * 顺数进度条，从0-100；
         */
        COUNT,

        /**
         * 倒数进度条，从100-0；
         */
        COUNT_BACK
    }

    interface OnCountdownProgressListener {
        fun onProgress(what: Int, progress: Int)
    }

}
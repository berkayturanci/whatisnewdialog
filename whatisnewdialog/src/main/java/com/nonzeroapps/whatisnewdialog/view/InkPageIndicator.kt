package com.nonzeroapps.whatisnewdialog.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager.widget.ViewPager
import com.nonzeroapps.whatisnewdialog.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by berkayturanci on 04/08/2017.
 */
class InkPageIndicator
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : View(context, attrs, defStyle),
        ViewPager.OnPageChangeListener,
        View.OnAttachStateChangeListener {
        private val selectedPaint: Paint
        private val unselectedDotPath: Path
        private val unselectedDotLeftPath: Path
        private val unselectedDotRightPath: Path
        private val rectF: RectF
        private val interpolator: Interpolator
        private val combinedUnselectedPath: Path

        private var endX1 = 0f
        private var endY1 = 0f
        private var endX2 = 0f
        private var endY2 = 0f
        private var controlX1 = 0f
        private var controlY1 = 0f
        private var controlX2 = 0f
        private var controlY2 = 0f

        private var dotDiameter: Int
        private var gap: Int
        private var animDuration: Long
        private var unselectedColour: Int = 0
        private var dotRadius: Float
        private var halfDotRadius: Float
        private var animHalfDuration: Long
        private var dotTopY = 0f
        private var dotCenterY = 0f
        private var dotBottomY = 0f
        private var viewPager: ViewPager? = null
        private var pageCount = 0
        private var currentPage = 0
        private var previousPage = 0
        private var selectedDotX = 0f
        private var selectedDotInPosition = false
        private var dotCenterX: FloatArray? = null
        private var joiningFractions: FloatArray? = null
        private var retreatingJoinX1 = 0f
        private var retreatingJoinX2 = 0f
        private var dotRevealFractions: FloatArray? = null
        private var attachedToWindow = false
        private var pageChanging = false
        private var unselectedPaint: Paint
        private var moveAnimation: ValueAnimator? = null
        private var retreatAnimation: PendingRetreatAnimator? = null
        private var revealAnimations: Array<PendingRevealAnimator?>? = null

        init {
            val density =
                context.resources.displayMetrics.density
                    .toInt()

            val typedArray =
                context.obtainStyledAttributes(
                    attrs,
                    R.styleable.InkPageIndicator,
                    defStyle,
                    0,
                )

            dotDiameter = typedArray.getDimensionPixelSize(R.styleable.InkPageIndicator_dotDiameter, DEFAULT_DOT_SIZE * density)
            dotRadius = (dotDiameter / 2).toFloat()
            halfDotRadius = dotRadius / 2
            gap = typedArray.getDimensionPixelSize(R.styleable.InkPageIndicator_dotGap, DEFAULT_GAP * density)
            animDuration = typedArray.getInteger(R.styleable.InkPageIndicator_animationDuration, DEFAULT_ANIM_DURATION).toLong()
            animHalfDuration = animDuration / 2
            unselectedColour = typedArray.getColor(R.styleable.InkPageIndicator_pageIndicatorColor, DEFAULT_UNSELECTED_COLOUR)
            val selectedColour = typedArray.getColor(R.styleable.InkPageIndicator_currentPageIndicatorColor, DEFAULT_SELECTED_COLOUR)
            typedArray.recycle()

            unselectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            unselectedPaint.color = unselectedColour
            selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            selectedPaint.color = selectedColour
            interpolator = FastOutSlowInInterpolator()

            combinedUnselectedPath = Path()
            unselectedDotPath = Path()
            unselectedDotLeftPath = Path()
            unselectedDotRightPath = Path()
            rectF = RectF()

            addOnAttachStateChangeListener(this)
        }

        private fun getCount(): Int = viewPager!!.adapter!!.count

        fun setViewPager(viewPager: ViewPager) {
            this.viewPager = viewPager
            viewPager.addOnPageChangeListener(this)
            setPageCount(getCount())
            viewPager.adapter!!.registerDataSetObserver(
                object : DataSetObserver() {
                    override fun onChanged() {
                        setPageCount(getCount())
                    }
                },
            )
            setCurrentPageImmediate()
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            if (attachedToWindow) {
                var fraction = positionOffset
                val currentPosition = if (pageChanging) previousPage else currentPage
                var leftDotPosition = position

                if (currentPosition != position) {
                    fraction = 1f - positionOffset

                    if (fraction == 1f) {
                        leftDotPosition = min(currentPosition, position)
                    }
                }
                setJoiningFraction(leftDotPosition, fraction)
            }
        }

        override fun onPageSelected(position: Int) {
            if (position < pageCount) {
                if (attachedToWindow) {
                    setSelectedPage(position)
                } else {
                    setCurrentPageImmediate()
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

        private fun setPageCount(pages: Int) {
            if (pages > 0) {
                pageCount = pages
                resetState()
                requestLayout()
            }
        }

        private fun calculateDotPositions(width: Int) {
            val left = paddingLeft
            val top = paddingTop
            val right = width - paddingRight

            val requiredWidth = getRequiredWidth()
            val startLeft = left + ((right - left - requiredWidth) / 2) + dotRadius

            dotCenterX = FloatArray(pageCount)
            for (i in 0 until pageCount) {
                dotCenterX!![i] = startLeft + i * (dotDiameter + gap)
            }
            dotTopY = top.toFloat()
            dotCenterY = top + dotRadius
            dotBottomY = (top + dotDiameter).toFloat()

            setCurrentPageImmediate()
        }

        private fun setCurrentPageImmediate() {
            currentPage = viewPager?.currentItem ?: 0
            if (isDotAnimationStarted()) {
                selectedDotX = dotCenterX!![currentPage]
            }
        }

        private fun isDotAnimationStarted(): Boolean =
            dotCenterX != null && dotCenterX!!.isNotEmpty() && (moveAnimation == null || !moveAnimation!!.isStarted)

        private fun resetState() {
            joiningFractions = FloatArray(pageCount - 1)
            dotRevealFractions = FloatArray(pageCount)
            retreatingJoinX1 = INVALID_FRACTION
            retreatingJoinX2 = INVALID_FRACTION
            selectedDotInPosition = true
        }

        @SuppressLint("SwitchIntDef")
        override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int,
        ) {
            val desiredHeight = getDesiredHeight()
            val height =
                when (MeasureSpec.getMode(heightMeasureSpec)) {
                    MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
                    MeasureSpec.AT_MOST -> min(desiredHeight, MeasureSpec.getSize(heightMeasureSpec))
                    else -> desiredHeight
                }

            val desiredWidth = getDesiredWidth()
            val width =
                when (MeasureSpec.getMode(widthMeasureSpec)) {
                    MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
                    MeasureSpec.AT_MOST -> min(desiredWidth, MeasureSpec.getSize(widthMeasureSpec))
                    else -> desiredWidth
                }
            setMeasuredDimension(width, height)
            calculateDotPositions(width)
        }

        private fun getDesiredHeight(): Int = paddingTop + dotDiameter + paddingBottom

        private fun getRequiredWidth(): Int = pageCount * dotDiameter + (pageCount - 1) * gap

        private fun getDesiredWidth(): Int = paddingLeft + getRequiredWidth() + paddingRight

        override fun onViewAttachedToWindow(view: View) {
            attachedToWindow = true
        }

        override fun onViewDetachedFromWindow(view: View) {
            attachedToWindow = false
        }

        override fun onDraw(canvas: Canvas) {
            if (viewPager == null || pageCount == 0) return
            drawUnselected(canvas)
            drawSelected(canvas)
        }

        private fun drawUnselected(canvas: Canvas) {
            combinedUnselectedPath.rewind()

            for (page in 0 until pageCount) {
                val nextXIndex = if (page == pageCount - 1) page else page + 1

                val unselectedPath =
                    getUnselectedPath(
                        page,
                        dotCenterX!![page],
                        dotCenterX!![nextXIndex],
                        if (page == pageCount - 1) INVALID_FRACTION else joiningFractions!![page],
                        dotRevealFractions!![page],
                    )

                unselectedPath.addPath(combinedUnselectedPath)
                combinedUnselectedPath.addPath(unselectedPath)
            }

            if (retreatingJoinX1 != INVALID_FRACTION) {
                val retreatingJoinPath = getRetreatingJoinPath()
                combinedUnselectedPath.addPath(retreatingJoinPath)
            }

            canvas.drawPath(combinedUnselectedPath, unselectedPaint)
        }

        private fun getUnselectedPath(
            page: Int,
            centerX: Float,
            nextCenterX: Float,
            joiningFraction: Float,
            dotRevealFraction: Float,
        ): Path {
            unselectedDotPath.rewind()

            if (isDotNotJoining(page, joiningFraction, dotRevealFraction)) {
                unselectedDotPath.addCircle(dotCenterX!![page], dotCenterY, dotRadius, Path.Direction.CW)
            }

            if (isDotJoining(joiningFraction)) {
                unselectedDotLeftPath.rewind()
                unselectedDotLeftPath.moveTo(centerX, dotBottomY)
                rectF.set(centerX - dotRadius, dotTopY, centerX + dotRadius, dotBottomY)
                unselectedDotLeftPath.arcTo(rectF, 90f, 180f, true)

                endX1 = centerX + dotRadius + (joiningFraction * gap)
                endY1 = dotCenterY
                controlX1 = centerX + halfDotRadius
                controlY1 = dotTopY
                controlX2 = endX1
                controlY2 = endY1 - halfDotRadius
                unselectedDotLeftPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX1,
                    endY1,
                )

                endX2 = centerX
                endY2 = dotBottomY
                controlX1 = endX1
                controlY1 = endY1 + halfDotRadius
                controlX2 = centerX + halfDotRadius
                controlY2 = dotBottomY
                unselectedDotLeftPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX2,
                    endY2,
                )

                unselectedDotPath.addPath(unselectedDotLeftPath)

                unselectedDotRightPath.rewind()
                unselectedDotRightPath.moveTo(nextCenterX, dotBottomY)

                rectF.set(nextCenterX - dotRadius, dotTopY, nextCenterX + dotRadius, dotBottomY)
                unselectedDotRightPath.arcTo(rectF, 90f, -180f, true)

                endX1 = nextCenterX - dotRadius - (joiningFraction * gap)
                endY1 = dotCenterY
                controlX1 = nextCenterX - halfDotRadius
                controlY1 = dotTopY
                controlX2 = endX1
                controlY2 = endY1 - halfDotRadius
                unselectedDotRightPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX1,
                    endY1,
                )

                endX2 = nextCenterX
                endY2 = dotBottomY
                controlX1 = endX1
                controlY1 = endY1 + halfDotRadius
                controlX2 = endX2 - halfDotRadius
                controlY2 = dotBottomY
                unselectedDotRightPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX2,
                    endY2,
                )
                unselectedDotPath.addPath(unselectedDotRightPath)
            }

            if (joiningFraction > 0.5f && joiningFraction < 1f && retreatingJoinX1 == INVALID_FRACTION) {
                val adjustedFraction = (joiningFraction - 0.2f) * 1.25f

                unselectedDotPath.moveTo(centerX, dotBottomY)
                rectF.set(centerX - dotRadius, dotTopY, centerX + dotRadius, dotBottomY)
                unselectedDotPath.arcTo(rectF, 90f, 180f, true)

                endX1 = centerX + dotRadius + (gap / 2)
                endY1 = dotCenterY - (adjustedFraction * dotRadius)
                controlX1 = endX1 - (adjustedFraction * dotRadius)
                controlY1 = dotTopY
                controlX2 = endX1 - ((1 - adjustedFraction) * dotRadius)
                controlY2 = endY1
                unselectedDotPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX1,
                    endY1,
                )

                endX2 = nextCenterX
                endY2 = dotTopY
                controlX1 = endX1 + ((1 - adjustedFraction) * dotRadius)
                controlY1 = endY1
                controlX2 = endX1 + (adjustedFraction * dotRadius)
                controlY2 = dotTopY
                unselectedDotPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX2,
                    endY2,
                )

                rectF.set(nextCenterX - dotRadius, dotTopY, nextCenterX + dotRadius, dotBottomY)
                unselectedDotPath.arcTo(rectF, 270f, 180f, true)

                endY1 = dotCenterY + (adjustedFraction * dotRadius)
                controlX1 = endX1 + (adjustedFraction * dotRadius)
                controlY1 = dotBottomY
                controlX2 = endX1 + ((1 - adjustedFraction) * dotRadius)
                controlY2 = endY1
                unselectedDotPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX1,
                    endY1,
                )

                endX2 = centerX
                endY2 = dotBottomY
                controlX1 = endX1 - ((1 - adjustedFraction) * dotRadius)
                controlY1 = endY1
                controlX2 = endX1 - (adjustedFraction * dotRadius)
                controlY2 = endY2
                unselectedDotPath.cubicTo(
                    controlX1,
                    controlY1,
                    controlX2,
                    controlY2,
                    endX2,
                    endY2,
                )
            }
            if (joiningFraction == 1f && retreatingJoinX1 == INVALID_FRACTION) {
                rectF.set(centerX - dotRadius, dotTopY, nextCenterX + dotRadius, dotBottomY)
                unselectedDotPath.addRoundRect(rectF, dotRadius, dotRadius, Path.Direction.CW)
            }

            if (dotRevealFraction > MINIMAL_REVEAL) {
                unselectedDotPath.addCircle(
                    centerX,
                    dotCenterY,
                    dotRevealFraction * dotRadius,
                    Path.Direction.CW,
                )
            }

            return unselectedDotPath
        }

        private fun isDotJoining(joiningFraction: Float): Boolean =
            joiningFraction > 0f && joiningFraction <= 0.5f && retreatingJoinX1 == INVALID_FRACTION

        private fun isDotNotJoining(
            page: Int,
            joiningFraction: Float,
            dotRevealFraction: Float,
        ): Boolean =
            (joiningFraction == 0f || joiningFraction == INVALID_FRACTION) &&
                dotRevealFraction == 0f &&
                !(page == currentPage && selectedDotInPosition)

        private fun getRetreatingJoinPath(): Path {
            unselectedDotPath.rewind()
            rectF.set(retreatingJoinX1, dotTopY, retreatingJoinX2, dotBottomY)
            unselectedDotPath.addRoundRect(rectF, dotRadius, dotRadius, Path.Direction.CW)
            return unselectedDotPath
        }

        private fun drawSelected(canvas: Canvas) {
            canvas.drawCircle(selectedDotX, dotCenterY, dotRadius, selectedPaint)
        }

        private fun setSelectedPage(now: Int) {
            if (now == currentPage) {
                return
            }

            pageChanging = true
            previousPage = currentPage
            currentPage = now
            val steps = abs(now - previousPage)

            if (steps > 1) {
                if (now > previousPage) {
                    for (i in 0 until steps) {
                        setJoiningFraction(previousPage + i, 1f)
                    }
                } else {
                    var i = -1
                    while (i > -steps) {
                        setJoiningFraction(previousPage + i, 1f)
                        i--
                    }
                }
            }

            moveAnimation = createMoveSelectedAnimator(dotCenterX!![now], previousPage, now, steps)
            moveAnimation!!.start()
        }

        private fun createMoveSelectedAnimator(
            moveTo: Float,
            was: Int,
            now: Int,
            steps: Int,
        ): ValueAnimator {
            val moveSelected = ValueAnimator.ofFloat(selectedDotX, moveTo)

            retreatAnimation =
                PendingRetreatAnimator(
                    was,
                    now,
                    steps,
                    if (now > was) {
                        RightwardStartPredicate(moveTo - ((moveTo - selectedDotX) * 0.25f))
                    } else {
                        LeftwardStartPredicate(moveTo + ((selectedDotX - moveTo) * 0.25f))
                    },
                )
            retreatAnimation!!.addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        resetState()
                        pageChanging = false
                    }
                },
            )
            moveSelected.addUpdateListener { valueAnimator ->
                selectedDotX = valueAnimator.animatedValue as Float
                retreatAnimation!!.startIfNecessary(selectedDotX)
                ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
            }
            moveSelected.addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        selectedDotInPosition = false
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        selectedDotInPosition = true
                    }
                },
            )

            moveSelected.startDelay = if (selectedDotInPosition) animDuration / 4L else 0L
            moveSelected.duration = animDuration * 3L / 4L
            moveSelected.interpolator = interpolator
            return moveSelected
        }

        private fun setJoiningFraction(
            leftDot: Int,
            fraction: Float,
        ) {
            joiningFractions?.let {
                if (leftDot < it.size) {
                    it[leftDot] = fraction
                    ViewCompat.postInvalidateOnAnimation(this)
                }
            }
        }

        fun clearJoiningFractions() {
            joiningFractions?.fill(0f)
            ViewCompat.postInvalidateOnAnimation(this)
        }

        private fun setDotRevealFraction(
            dot: Int,
            fraction: Float,
        ) {
            dotRevealFractions?.let {
                if (dot < it.size) {
                    it[dot] = fraction
                }
            }
            ViewCompat.postInvalidateOnAnimation(this)
        }

        fun setPageIndicatorColor(secondaryColor: Int) {
            unselectedColour = secondaryColor
            unselectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            unselectedPaint.color = unselectedColour
        }

        override fun onRestoreInstanceState(state: Parcelable) {
            val savedState = state as SavedState
            super.onRestoreInstanceState(savedState.superState)
            currentPage = savedState.currentPage
            requestLayout()
        }

        override fun onSaveInstanceState(): Parcelable {
            val superState = super.onSaveInstanceState()
            val savedState = SavedState(superState)
            savedState.currentPage = currentPage
            return savedState
        }

        internal class SavedState : BaseSavedState {
            var currentPage = 0

            constructor(superState: Parcelable?) : super(superState)

            private constructor(parcel: Parcel) : super(parcel) {
                currentPage = parcel.readInt()
            }

            override fun writeToParcel(
                dest: Parcel,
                flags: Int,
            ) {
                super.writeToParcel(dest, flags)
                dest.writeInt(currentPage)
            }

            companion object {
                @JvmField
                val CREATOR: Parcelable.Creator<SavedState> =
                    object : Parcelable.Creator<SavedState> {
                        override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

                        override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
                    }
            }
        }

        abstract inner class PendingStartAnimator(
            var predicate: StartPredicate,
        ) : ValueAnimator() {
            var hasStarted = false

            fun startIfNecessary(currentValue: Float) {
                if (!hasStarted && predicate.shouldStart(currentValue)) {
                    start()
                    hasStarted = true
                }
            }
        }

        inner class PendingRetreatAnimator(
            was: Int,
            now: Int,
            steps: Int,
            predicate: StartPredicate,
        ) : PendingStartAnimator(predicate) {
            init {
                duration = animHalfDuration
                interpolator = this@InkPageIndicator.interpolator

                // work out the start/end values of the retreating join from the direction we're
                // travelling in.  Also look at the current selected dot position, i.e. we're moving on
                // before a prior anim has finished.
                val initialX1 =
                    if (now > was) {
                        min(dotCenterX!![was], selectedDotX) - dotRadius
                    } else {
                        dotCenterX!![now] - dotRadius
                    }
                val finalX1 =
                    if (now > was) dotCenterX!![now] - dotRadius else dotCenterX!![now] - dotRadius
                val initialX2 =
                    if (now > was) {
                        dotCenterX!![now] + dotRadius
                    } else {
                        max(dotCenterX!![was], selectedDotX) + dotRadius
                    }
                val finalX2 =
                    if (now > was) dotCenterX!![now] + dotRadius else dotCenterX!![now] + dotRadius

                revealAnimations = arrayOfNulls(steps)
                // hold on to the indexes of the dots that will be hidden by the retreat so that
                // we can initialize their revealFraction's i.e. make sure they're hidden while the
                // reveal animation runs
                val dotsToHide = IntArray(steps)
                if (initialX1 != finalX1) {
                    setFloatValues(initialX1, finalX1)
                    for (i in 0 until steps) {
                        revealAnimations!![i] =
                            PendingRevealAnimator(
                                was + i,
                                RightwardStartPredicate(dotCenterX!![was + i]),
                            )
                        dotsToHide[i] = was + i
                    }
                    addUpdateListener { valueAnimator ->
                        retreatingJoinX1 = valueAnimator.animatedValue as Float
                        ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)

                        revealAnimations?.forEach { pendingReveal ->
                            pendingReveal?.startIfNecessary(retreatingJoinX1)
                        }
                    }
                } else {
                    setFloatValues(initialX2, finalX2)
                    for (i in 0 until steps) {
                        revealAnimations!![i] =
                            PendingRevealAnimator(
                                was - i,
                                LeftwardStartPredicate(dotCenterX!![was - i]),
                            )
                        dotsToHide[i] = was - i
                    }
                    addUpdateListener { valueAnimator ->
                        retreatingJoinX2 = valueAnimator.animatedValue as Float
                        ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)

                        revealAnimations?.forEach { pendingReveal ->
                            pendingReveal?.startIfNecessary(retreatingJoinX2)
                        }
                    }
                }

                addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            clearJoiningFractions()

                            for (dot in dotsToHide) {
                                setDotRevealFraction(dot, MINIMAL_REVEAL)
                            }
                            retreatingJoinX1 = initialX1
                            retreatingJoinX2 = initialX2
                            ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            retreatingJoinX1 = INVALID_FRACTION
                            retreatingJoinX2 = INVALID_FRACTION
                            ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                        }
                    },
                )
            }
        }

        inner class PendingRevealAnimator(
            private val dot: Int,
            predicate: StartPredicate,
        ) : PendingStartAnimator(predicate) {
            init {
                setFloatValues(MINIMAL_REVEAL, 1f)
                duration = animHalfDuration
                interpolator = this@InkPageIndicator.interpolator
                addUpdateListener { valueAnimator ->
                    setDotRevealFraction(dot, valueAnimator.animatedValue as Float)
                }
                addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            setDotRevealFraction(dot, 0f)
                            ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                        }
                    },
                )
            }
        }

        abstract inner class StartPredicate(
            var thresholdValue: Float,
        ) {
            abstract fun shouldStart(currentValue: Float): Boolean
        }

        inner class RightwardStartPredicate(
            thresholdValue: Float,
        ) : StartPredicate(thresholdValue) {
            override fun shouldStart(currentValue: Float): Boolean = currentValue > thresholdValue
        }

        inner class LeftwardStartPredicate(
            thresholdValue: Float,
        ) : StartPredicate(thresholdValue) {
            override fun shouldStart(currentValue: Float): Boolean = currentValue < thresholdValue
        }

        companion object {
            private const val DEFAULT_DOT_SIZE = 8
            private const val DEFAULT_GAP = 12
            private const val DEFAULT_ANIM_DURATION = 400
            private val DEFAULT_UNSELECTED_COLOUR = 0x80ffffff.toInt()
            private val DEFAULT_SELECTED_COLOUR = 0xffffffff.toInt()

            private const val INVALID_FRACTION = -1f
            private const val MINIMAL_REVEAL = 0.00001f
        }
    }

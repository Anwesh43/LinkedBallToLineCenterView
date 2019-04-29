package com.anwesh.uiprojects.balltolinecenterview

/**
 * Created by anweshmishra on 30/04/19.
 */

import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Paint
import android.content.Context
import android.app.Activity
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val parts : Int = 2
val subParts : Int = 3
val scGap : Float = 0.05f
val scDiv : Double = 0.51
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#673AB7")
val backColor : Int = Color.parseColor("#BDBDBD")
val rFactor : Float = 7f
val circleOffsetFactor : Float = 0.7f

fun Int.inverse() : Float = 1f / this
fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.mirrorValue(a : Int, b : Int) : Float {
    val k : Float = scaleFactor()
    return (1 - k) * a.inverse() + k * b.inverse()
}
fun Float.updateValue(dir : Float, a : Int, b : Int) : Float = mirrorValue(a, b) * dir * scGap

fun Canvas.drawBallToLineCenter(size : Float, sc : Float, paint : Paint) {
    val r : Float = size / rFactor
    val y : Float = size * circleOffsetFactor
    val y1 : Float = y * sc.divideScale(2, subParts)
    save()
    rotate(180f * sc.divideScale(0, subParts))
    drawCircle(0f, y - y1, r, paint)
    paint.strokeWidth = 2 * r
    drawLine(0f, y - y1, 0f, y - y * sc.divideScale(1, subParts), paint)
    restore()
}

fun Canvas.drawBTLCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    for (j in 0..(parts - 1)) {
        val sc : Float = scale.divideScale(j, parts)
        save()
        scale(1f - 2 * j, 1f)
        drawBallToLineCenter(size, sc, paint)
        restore()
    }
}

class BallToLineCenterView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN  ->  {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateValue(dir, parts, subParts)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BTLCNode(var i : Int, val state : State = State()) {

        private var next : BTLCNode? = null
        private var prev : BTLCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BTLCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBTLCNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BTLCNode {
            var curr : BTLCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BallToLineCenter(var i : Int) {

        private val root : BTLCNode = BTLCNode(0)
        private var curr : BTLCNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BallToLineCenterView) {

        private val animator : Animator = Animator(view)
        private val bltc : BallToLineCenter = BallToLineCenter(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bltc.draw(canvas, paint)
            animator.animate {
                bltc.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bltc.startUpdating {
                animator.start()
            }
        }
    }
}
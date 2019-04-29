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
    drawLine(0f, y - y1, 0f, y - y * sc.divideScale(1, subParts))
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

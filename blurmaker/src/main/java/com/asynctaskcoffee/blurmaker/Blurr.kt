package com.asynctaskcoffee.blurmaker

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.widget.ImageView
import com.asynctaskcoffee.blurmaker.Blurr.Tools.Companion.bitmapFromCustomView
import com.asynctaskcoffee.blurmaker.Blurr.Tools.Companion.bitmapFromDrawable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.RuntimeException
import kotlin.math.roundToInt

class Blurr(private var activity: Activity? = null) {

    private var bS = 0.3f
    private var bR = 15f

    companion object {
        fun get(activity: Activity): Blurr = Blurr(activity)
        fun getTools(): Tools = Tools()
    }

    /**
     * @param bitmapScale should be 0-1f -> small values for more blur
     * @param blurRadius should be 0-25f -> bigger values for more blur
     *
     * @exception RSIllegalArgumentException Radius out of range (0 < r <= 25)
     * @exception NullPointerException void android.graphics.Bitmap.setHasAlpha(boolean)
     *
     * */
    fun applyRules(bitmapScale: Float, blurRadius: Float): Blurr {
        bS = bitmapScale
        bR = blurRadius
        return this
    }

    fun solution(v: View) = bitmapFromCustomView(v)?.let { gb(it) }

    fun solution(b: Bitmap) = gb(b)

    fun solution(d: Drawable) = bitmapFromDrawable(d)?.let { gb(it) }

    fun into(v: View, i: ImageView) {
        bitmapFromCustomView(v)?.let { rx(it, i) }
    }

    fun into(b: Bitmap, i: ImageView) {
        rx(b, i)
    }

    fun into(d: Drawable, i: ImageView) {
        bitmapFromDrawable(d)?.let { rx(it, i) }
    }


    /**
     * Creates blurred bitmap from target bitmap
     * */
    private fun gb(image: Bitmap): Bitmap {
        if (activity == null) throw RuntimeException("you have to implement activity")
        val width = (image.width * bS).roundToInt()
        val height = (image.height * bS).roundToInt()
        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = RenderScript.create(activity)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(bR)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }

    private fun rx(image: Bitmap, imageView: ImageView) {
        if (activity == null) throw RuntimeException("you have to implement activity")
        Flowable.fromCallable {
            return@fromCallable gb(image)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe {
                activity?.runOnUiThread {
                    imageView.setImageBitmap(it)
                }
            }
    }


    class Tools {
        /**
         * Creates bitmap from drawable
         * */
        fun bitmapFromDrawable(drawable: Drawable): Bitmap? {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap? =
                if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                    Bitmap.createBitmap(
                        1,
                        1,
                        Bitmap.Config.ARGB_8888
                    )
                } else {
                    Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                }
            val canvas = bitmap?.let { Canvas(it) }
            if (canvas != null) {
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
            return bitmap
        }


        /**
         * Creates bitmap from View
         * */
        fun bitmapFromCustomView(view: View): Bitmap? {
            val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(spec, spec)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            val b = Bitmap.createBitmap(
                view.measuredWidth, view.measuredWidth,
                Bitmap.Config.ARGB_8888
            )
            val c = Canvas(b)
            c.translate((-view.scrollX).toFloat(), (-view.scrollY).toFloat())
            view.draw(c)
            return b
        }
    }
}
package com.asynctaskcoffee.blurryandroid

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.asynctaskcoffee.blurmaker.Blurr
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private var bS = 0.3f
    private var bR = 15f
    private var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arrangeAgain()
        setListeners()
    }

    private fun exampleOfUsages() {

        /**
         * Blurred Images
         * */

        Blurr.get(this)
            .applyRules(bS, bR)
            .into(resources.getDrawable(R.drawable.test_image), imageViewTrial) //async

        Blurr.get(this)
            .applyRules(bS, bR)
            .into(view, imageViewTrial) //async

        Blurr.get(this)
            .applyRules(bS, bR)
            .into(bitmap!!, imageViewTrial) //async

        val bitmap1 = Blurr
            .get(this)
            .applyRules(bS, bR)
            .solution(view)

        val bitmap2 = Blurr
            .get(this)
            .applyRules(bS, bR)
            .solution(resources.getDrawable(R.drawable.test_image))

        val bitmap3 = Blurr
            .get(this)
            .applyRules(bS, bR)
            .solution(bitmap!!)

        /**
         * Inline tools
         * */

        val bitmap4 = Blurr
            .getTools()
            .bitmapFromCustomView(view)

        val bitmap5 = Blurr
            .getTools()
            .bitmapFromDrawable(resources.getDrawable(R.drawable.test_image))
    }

    private fun setListeners() {
        bitmapScale.setOnSeekBarChangeListener(this)
        blurRadius.setOnSeekBarChangeListener(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun arrangeAgain() {
        Blurr.get(this)
            .applyRules(bS, bR)
            .into(resources.getDrawable(R.drawable.test_image), imageViewTrial)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val value = 1 + progress
        when (seekBar?.id) {
            R.id.blurRadius -> {
                bR = (value.toFloat())
            }
            R.id.bitmapScale -> {
                bS = (value.toFloat() / 100)
            }
        }
        arrangeAgain()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}
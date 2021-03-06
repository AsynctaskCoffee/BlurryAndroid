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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arrangeAgain()
        setListeners()
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
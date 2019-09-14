package com.sha.glassify

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sha.glassify.helper.ArHelper
import com.sha.glassify.helper.ArPhotoTaker


class ARFaceActivity : AppCompatActivity() {

    private lateinit var arFragment: FaceARFragment
    private val helper = ArHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
    }

    private fun setup() {
        if (!helper.isDeviceSupported(this)) {
            finish()
            return
        }

        setContentView(R.layout.activity_arface)

        arFragment = supportFragmentManager.findFragmentById(R.id.face_fragment) as FaceARFragment
        helper.setupScene(arFragment)

        setupUi()
    }

    private fun setupUi() {
        findViewById<ImageView>(R.id.ivWhiteGlasses).setOnClickListener {
            helper.loadModel(R.raw.sunglasses)
        }

        findViewById<ImageView>(R.id.ivBlackGlasses).setOnClickListener {
            helper.loadModel(R.raw.sunglasses_01)
        }

        findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                helper.scale(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            ArPhotoTaker.take(arFragment, this)
        }
    }

}

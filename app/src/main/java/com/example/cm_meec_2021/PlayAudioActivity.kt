package com.example.cm_meec_2021

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class PlayAudioActivity : AppCompatActivity() {

    lateinit var imagePlayButton: ImageButton
    lateinit var imageDeleteButton: ImageButton
    lateinit var cirprogrBar: CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_audio)

        imagePlayButton = findViewById(R.id.imagePlayButton)
        imageDeleteButton = findViewById(R.id.imageDeleteButton)
        cirprogrBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)

        imagePlayButton.setOnClickListener() {
            cicularProgressbar()
            cirprogrBar.setProgressWithAnimation(100f, 10000)

        }


        imageDeleteButton.setOnClickListener() {

        }

    }


    private fun cicularProgressbar(){

        cirprogrBar.apply {
            progressMax = 100f

            progressBarWidth = 20f

            progressBarColorStart = Color.GRAY
            progressBarColorEnd = Color.DKGRAY
            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            backgroundProgressBarColorStart = Color.BLACK

            roundBorder = true
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT

            //https://github.com/lopspower/CircularProgressBar

        }

        cirprogrBar.onProgressChangeListener = {progress->

        }
    }
}

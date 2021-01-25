package com.example.cm_meec_2021

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class PlayAudioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    //private lateinit var reference: DatabaseReference
    lateinit var cirprogrBar: CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_audio)

        val audioNumber = intent.getStringExtra("audioNumber")
        val filename = intent.getStringExtra("filename")


        database = FirebaseDatabase.getInstance()
        //reference = database.getReference("Users")
        auth = FirebaseAuth.getInstance()

        cirprogrBar = findViewById(R.id.circularProgressBar)
        val imagePlayButton = findViewById<ImageButton>(R.id.imagePlayButton)
        val imageDeleteButton = findViewById<ImageButton>(R.id.imageDeleteButton)
        val pageTitle = findViewById<TextView>(R.id.textView)
        val audioClass = findViewById<TextView>(R.id.audioClassText)

        pageTitle.text = "Audio $audioNumber"


        //get audio URL
        //use this to play the audio file from firebase by accessing the child and getting the url string with a specific filename
        var url = ""
        val id=auth.currentUser?.uid
        val audioRef = database.reference.child("Users").child("$id").child("Audios").child("$filename")
        //val audioRef = database.reference.child("Users/$id/Audios/$filename")

        audioRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p1: DatabaseError) {
            }
            override fun onDataChange(p1: DataSnapshot) {
                println("teste--------------"+p1.toString())
                if(p1.value != null) {  //if(filename != null)
                    println("teste--------------"+p1.toString())
                    val map = p1.value as Map<String, Any>
                    url=map["url"].toString()
                    audioClass.text = "Classe: ${map["classValue"]}"
                }
            }
        })

        imagePlayButton.setOnClickListener() {

            //play audio
            val playAudio = MediaPlayer()
            playAudio.setDataSource(url)
            playAudio.prepare()
            playAudio.start()

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

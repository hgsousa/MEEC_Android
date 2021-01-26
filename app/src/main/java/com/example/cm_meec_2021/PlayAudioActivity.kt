package com.example.cm_meec_2021

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


class PlayAudioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    //private lateinit var reference: DatabaseReference
    lateinit var cirprogrBar: CircularProgressBar
    //lateinit var imageShareButton: ImageButton
    val playAudio = MediaPlayer()
    var cirprogrBarAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_audio)

        supportActionBar?.hide()

        val audioNumber = intent.getStringExtra("audioNumber")
        val filename = intent.getStringExtra("filename")


        database = FirebaseDatabase.getInstance()
        //reference = database.getReference("Users")
        auth = FirebaseAuth.getInstance()

        cirprogrBar = findViewById(R.id.circularProgressBar)
        val imagePlayButton = findViewById<ImageButton>(R.id.imagePlayButton)
        val imageDeleteButton = findViewById<ImageButton>(R.id.imageDeleteButton)
        val imageShareButton = findViewById<ImageButton>(R.id.imageShareButton)
        val pageTitle = findViewById<TextView>(R.id.textView)
        val audioClassTextView = findViewById<TextView>(R.id.audioClassText)

        pageTitle.text = "Audio $audioNumber"


        //get audio URL
        //use this to play the audio file from firebase by accessing the child and getting the url string with a specific filename
        var url = ""
        var audioClass = ""
        val id=auth.currentUser?.uid
        val audioRef = database.reference
            .child("Users")
            .child("$id")
            .child("Audios")
            .child("$filename")
        //val audioRef = database.reference.child("Users/$id/Audios/$filename")

        audioRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p1: DatabaseError) {
            }
            override fun onDataChange(p1: DataSnapshot) {
                //println("teste--------------"+p1.toString())
                if(p1.value != null) {  //if(filename != null)
                   //println("teste--------------"+p1.toString())
                    val map = p1.value as Map<String, Any>
                    url=map["url"].toString()
                    audioClass = map["classValue"].toString()
                    audioClassTextView.text = "Class: $audioClass"
                }
            }
        })

        imagePlayButton.setOnClickListener() {

            //play audio
            playAudio.reset()
            playAudio.setDataSource(url)
            playAudio.prepare()
            playAudio.start()

            cicularProgressbar()
            //cirprogrBar.setProgressWithAnimation(playAudio.currentPosition.toFloat(), playAudio.duration.toFloat())
            cirprogrBarAnimation=true
        }
        /*
        audioClassTextView.text = playAudio.currentPosition.toFloat().toString()
        cirprogrBar.setProgressWithAnimation(playAudio.currentPosition.toFloat(), 10000)
*/
        imageDeleteButton.setOnClickListener() {

            val id=auth.currentUser?.uid
            database.reference
                .child("Users")
                .child("$id")
                .child("Audios")
                .child("$filename")
                .removeValue()
            onBackPressed()
        }

        imageShareButton.setOnClickListener(){

            val AudioMessage: String = "User: ${auth.currentUser?.displayName}\n" +"\tAudio $audioNumber\n" +
                    "\tFilename: $filename\n" +
                    "\tClass value: $audioClass\n" +
                    "\tURL: $url"

            generateNoteOnSD(this, "AudioReport.txt", AudioMessage)

            //var file = Uri.fromFile(File(Environment.getExternalStorageDirectory().toString()+"/MEEC_Android/AudioReport.txt"))
            val file = Uri.fromFile(File(Environment.getExternalStorageDirectory(), "MEEC_Android/AudioReport.txt"))
            //Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"

            val intent = Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,file)     //DOESNT WORK
            intent.type="*/*"
            /*
            intent.putExtra(Intent.EXTRA_TEXT,file)
            intent.type="text/plain"
            */

            startActivity(Intent.createChooser(intent,"Share to:"))
        }

    }

    fun generateNoteOnSD(context: Context?, sFileName: String?, sBody: String?) {
        try {
            val root =
                File(Environment.getExternalStorageDirectory(), "MEEC_Android")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile = File(root, sFileName)
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //audio stop if user gets out of the activity
    override fun onBackPressed(){
        playAudio.stop()
        val intentback = Intent(this, AudioListActivity::class.java)
        startActivity(intentback)
        finish()
    }
    override fun onPause(){
        super.onPause()
        playAudio.stop()
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

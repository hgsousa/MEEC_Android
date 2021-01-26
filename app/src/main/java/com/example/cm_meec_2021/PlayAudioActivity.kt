package com.example.cm_meec_2021

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.Exception


class PlayAudioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    //private lateinit var reference: DatabaseReference
    lateinit var cirprogrBar: CircularProgressBar
    lateinit var seekBar: SeekBar

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
        storage = FirebaseStorage.getInstance()
        //reference = database.getReference("Users")
        auth = FirebaseAuth.getInstance()

        //cirprogrBar = findViewById(R.id.circularProgressBar)
        val imagePlayButton = findViewById<FloatingActionButton>(R.id.imagePlayButton)

        seekBar = findViewById<SeekBar>(R.id.seekbar)

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
                    val date = p1.key?.substring(6,8)+"/"+p1.key?.substring(4,6)+"/"+p1.key?.substring(0,4)
                    val time = p1.key?.substring(13,15)+":"+p1.key?.substring(11,13)+":"+p1.key?.substring(9,11)
                    audioClassTextView.text = "Date: $date"+ "\n\nTime: $time"+"\n\nClass: $audioClass"

                }
            }
        })

        //media buttons ----------------------------------------------------------------------------
        imagePlayButton.setOnClickListener() {

            //play audio
            playAudio.reset()
            playAudio.setDataSource(url)
            playAudio.prepare()
            playAudio.start()

            initialiseSeekBar()
        }


        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)playAudio?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })





        // ----------------------------------------------------------------------------


        imageDeleteButton.setOnClickListener() {

            //delete from database
            val id=auth.currentUser?.uid
            database.reference
                .child("Users")
                .child("$id")
                .child("Audios")
                .child("$filename")
                .removeValue()

            //delete from storage
            val storageRef = storage.reference
                    .child("Users")
                    .child("$id")
                    .child("Audios")
                    .child("SOUND_${filename}_.mp3")
            //println("------------------SOUND_${filename}_.mp3")

            storageRef.delete().addOnSuccessListener {
                Toast.makeText(this, "Storage delete Successful", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Storage delete Failed", Toast.LENGTH_LONG).show()
            }

            onBackPressed()
        }

        imageShareButton.setOnClickListener(){

            val AudioMessage: String = "User: ${auth.currentUser?.displayName}\n" +"\tAudio $audioNumber\n" +
                    "\tFilename: $filename\n" +
                    "\tClass value: $audioClass\n" +
                    "\tURL: $url"

            generateNoteOnSD(this, "AudioReport.txt", AudioMessage)

            //var file = Uri.fromFile(File(Environment.getExternalStorageDirectory().toString()+"/MEEC_Android/AudioReport.txt"))
            //val file = Uri.fromFile(File(Environment.getExternalStorageDirectory(), "MEEC_Android/AudioReport.txt"))
            //Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
            val fileUri = FileProvider.getUriForFile(this, "com.example.cm_meec_2021.provider",
                    File(Environment.getExternalStorageDirectory(), "MEEC_Android/AudioReport.txt"))
            val intent = Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_STREAM,fileUri)
            intent.type="text/*"
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


    private fun initialiseSeekBar(){
        seekBar.max = playAudio!!.duration
        val handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                try {
                    seekBar.progress = playAudio!!.currentPosition
                    handler.postDelayed(this, 1000)
                }catch(e:Exception){
                    seekBar.progress = 0
                }

            }
        },0)
    }


}

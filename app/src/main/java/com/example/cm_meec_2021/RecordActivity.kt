package com.example.cm_meec_2021

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Gallery
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cm_meec_2021.R
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class RecordActivity : AppCompatActivity() {
    private lateinit var recordAudio : MediaRecorder
    lateinit var buttonStart:Button
    lateinit var buttonStop:Button
    lateinit var buttonPlay:Button
    lateinit var buttonUpload:Button

    lateinit var  soundUri:Uri

    private lateinit var mStorage: StorageReference;   //Firebase storage

    val AUDIO = 0
    val GALLERY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStop = findViewById<Button>(R.id.buttonStop)
        buttonPlay = findViewById<Button>(R.id.buttonPlay)
        buttonUpload = findViewById<Button>(R.id.buttonUpload)

        var timerView = findViewById<TextView>(R.id.timerView)

        var path= Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"

        recordAudio = MediaRecorder()

        buttonStart.isEnabled = false
        buttonStop.isEnabled = false



        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)     //se permissão não for aceite pelo utilizador
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),111)
        buttonStart.isEnabled = true

        val timerViewer = object: CountDownTimer(10000, 1000){
            override fun onTick(i: Long) {
                timerView.text="Time: ${i/1000} sec"
            }
            override fun onFinish() {
                timerView.text=""
            }
        }

        //inicio de gravação
        buttonStart.setOnClickListener{
            recordAudio.setAudioSource(MediaRecorder.AudioSource.MIC)
            recordAudio.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recordAudio.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recordAudio.setMaxDuration(10000)  //Maximo de 10sec de gravação
            recordAudio.setOutputFile(path)
            recordAudio.prepare()
            recordAudio.start()
            buttonStop.isEnabled = true
            buttonStart.isEnabled = false

            timerViewer.start()
        }

        //Parar gravação
        buttonStop.setOnClickListener{
            recordAudio.stop()
            buttonStart.isEnabled = true
            buttonStop.isEnabled = false

            timerViewer.cancel()
            timerView.text= null
        }

        // Ouvir gravação
        buttonPlay.setOnClickListener{
            var playAudio = MediaPlayer()
            playAudio.setDataSource(path)
            playAudio.prepare()
            playAudio.start()
        }


        buttonUpload.setOnClickListener {

        }




    }


    fun openAlbum(view: View){
        var intent = Intent(Intent.ACTION_PICK)
        intent.setType("audio/*")
        startActivityForResult(intent,AUDIO)

    }



    fun uploadAudio(soundUri: Uri){
        var timestamp:String = SimpleDateFormat("yyyyMMdd").format(Date())
        var filename:String = "SOUND_"+timestamp+"_.mp3"
        var storageRef = FirebaseStorage.getInstance().reference
            .child("sound")
            .child(filename)
        storageRef.putFile(soundUri).addOnSuccessListener{
            Toast.makeText(this,"Uploaded to Firebase",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUDIO){
            var audioUri: Uri = data?.data!!
            //album_imageview.set
            uploadAudio(soundUri)
        }
    }



    fun onClickButtonDownload(){

    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            buttonPlay.isEnabled = true
    }


}







/*
        // Upload to firebase
        buttonUpload.setOnClickListener{
            view:View? -> val intent = Intent()
            intent.setType("audio/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Audio"),AUDIO)
        }

        fun upload() {
            var mReference = mStorage.child(url.lastPathSegment)
            try {
                mReference.putFile(uri).addOnSuccessListener {
                    taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.dowloadUrl
                    val Txtdownload = findViewById<View>(R.id.TextDownload) as TextView
                    Txtdownload.text = url.toString()
                    Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
                }
            }catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

        */
      */

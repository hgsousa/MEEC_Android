package com.example.cm_meec_2021

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaRecorder.OutputFormat.MPEG_4
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cm_meec_2021.R
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
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
    private lateinit var database: FirebaseDatabase
    private lateinit var mStorage: StorageReference;   //Firebase storage
    lateinit var uri: Uri
    var path:String = Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
    var timestamp:String = ""
    val id = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)


        buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStop = findViewById<Button>(R.id.buttonStop)
        buttonPlay = findViewById<Button>(R.id.buttonPlay)
        buttonUpload = findViewById<Button>(R.id.buttonUpload)

        mStorage = FirebaseStorage.getInstance().getReference("Audios")

        var timerView = findViewById<TextView>(R.id.timerView)

        //var path= Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"

        recordAudio = MediaRecorder()

        buttonStart.isEnabled = false
        buttonStop.isEnabled = false

        /*
       val mVisualizer = findViewById<BarVisualizer>(R.id.aVisualizer);
        val audioSessionId: Int = recordAudio.getAudioSessionId()
        if (audioSessionId != -1)
            mVisualizer.setAudioSessionId(audioSessionId)
        */

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
            recordAudio.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)  //more optimized for mobile phones than MPEG_4
            recordAudio.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recordAudio.setMaxDuration(10000)  //Maximo de 15sec de gravação
            recordAudio.setOutputFile(path)
            recordAudio.prepare()
            recordAudio.start()
            buttonStop.isEnabled = true
            buttonStart.isEnabled = false

            timerViewer.start()

            //upload do audio
/*
            val storage = FirebaseStorage.getInstance()
            storage.reference.child("Audios/a.mp3").downloadUrl
                    .addOnSuccessListener({
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(it.toString())
                mediaPlayer.setOnPreparedListener { player ->
                    player.start()
                }
                mediaPlayer.prepareAsync()
            })
*/
        }


        //Parar gravação
        buttonStop.setOnClickListener{
            recordAudio.stop()
            buttonStart.isEnabled = true
            buttonStop.isEnabled = false

            timerViewer.cancel()
            timerView.text= null
            timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())  //save time for filename

            //uri
            val uriTxt = findViewById<View>(R.id.uriTxt) as TextView
            uriTxt.text =Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
        }

        // Ouvir gravação
        buttonPlay.setOnClickListener{
            var playAudio = MediaPlayer()
            playAudio.setDataSource(path)
            playAudio.prepare()
            playAudio.start()
        }

        buttonUpload.setOnClickListener(){

            uploadToStorage()

            Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
        }

    }

    fun onClickDwnl(view: View){


        var filename:String = "SOUND_"+timestamp+"_.mp3"

        // Create a storage reference from our app
        val storageRef = FirebaseStorage.getInstance().reference.child("Users/$id/Audios/$filename")
        var file = Uri.fromFile(File(path))
        var uploadTask: StorageTask<*>
        uploadTask = storageRef.putFile(file)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {//put the storage photo link in the realtime database
                val downloadurl = uploadTask.result
                val url = downloadurl.toString()

                val map = HashMap<String, Any>()
                map[timestamp] = url
                FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child("$id")
                        .child("Audios")
                        .updateChildren(map)

                storageRef.downloadUrl.addOnSuccessListener { url ->//put the photo on the imageview
                    //url
                    val urlTxt = findViewById<View>(R.id.dwnTxt) as TextView
                    urlTxt.text = url.toString()

                    Toast.makeText(this, "Successfully Uploaded and downloaded", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "did nothing", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadToStorage(){

        //var path= Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
        //filename from the top
        var filename:String = "SOUND_"+timestamp+"_.mp3"

        // Create a storage reference from our app
        val storageRef = FirebaseStorage.getInstance().reference.child("Users/$id/Audios/$filename")
        var file = Uri.fromFile(File(path))
        storageRef.putFile(file)
/*
        var uploadTask: StorageTask<*>
        uploadTask = fileref.putFile(imageUri!!)

        uploadTask.addOnCompleteListener { task ->
            if(task.isSuccessful){//put the storage photo link in the realtime database
                val downloadurl=uploadTask.result
                val url =downloadurl.toString()

                val mapprofile=HashMap<String,Any>()
                mapprofile["profilepic"]=url
                FirebaseDatabase.getInstance().reference
                        .child("users")
                        .child("$id")
                        .updateChildren(mapprofile)

                fileref.downloadUrl.addOnSuccessListener { uri->//put the photo on the imageview
                    Glide.with(this).load(uri).apply(RequestOptions.circleCropTransform()).into(image)
                }
            }
            */
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            buttonPlay.isEnabled = true
    }
}

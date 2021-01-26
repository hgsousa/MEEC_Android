package com.example.cm_meec_2021

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaRecorder.OutputFormat.MPEG_4
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Build
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cm_meec_2021.R
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
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
    lateinit var buttonPlay:Button
    lateinit var buttonUpload:Button
    private lateinit var database: FirebaseDatabase
    private lateinit var mStorage: StorageReference;   //Firebase storage
    lateinit var uri: Uri
    var path:String = Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
    var timestamp:String = ""
    val id = FirebaseAuth.getInstance().currentUser?.uid
    var playAudio = MediaPlayer()

    private val CHANNEL_ID = "channel_id_example"
    private val notificationId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        supportActionBar?.hide()

        buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonPlay = findViewById<Button>(R.id.buttonPlay)
        buttonUpload = findViewById<Button>(R.id.buttonUpload)

        mStorage = FirebaseStorage.getInstance().getReference("Audios")

        var timerView = findViewById<TextView>(R.id.timerView)

        //var path= Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"

        recordAudio = MediaRecorder()

        buttonStart.isEnabled = false
        buttonUpload.isEnabled = false
        buttonPlay.isEnabled = false


        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)     //se permissão não for aceite pelo utilizador
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),111)
        buttonStart.isEnabled = true

        val timerViewer = object: CountDownTimer(10000, 1000){
            override fun onTick(i: Long) {

                timerView.text="Time: ${i/1000} sec"
            }
            override fun onFinish() { //recording finished

                timerView.text=""
                recordAudio.stop()
                buttonStart.isEnabled = true
                buttonUpload.isEnabled = true
                buttonPlay.isEnabled = true

                timerView.text= null
                timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())  //save time for filename

                //uri
                val uriTxt = findViewById<View>(R.id.uriTxt) as TextView
                uriTxt.text =Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
            }
        }

        //inicio de gravação
        buttonStart.setOnClickListener{
            recordAudio.setAudioSource(MediaRecorder.AudioSource.MIC)
            recordAudio.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)  //more optimized for mobile phones than MPEG_4
            recordAudio.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recordAudio.setMaxDuration(10000)
            recordAudio.setOutputFile(path)
            recordAudio.prepare()
            recordAudio.start()
            //buttonStop.isEnabled = false
            buttonStart.isEnabled = false

            timerViewer.start()

        }
       /* buttonStop.setOnClickListener{
            recordAudio.stop()
            buttonStart.isEnabled = true
            buttonStop.isEnabled = false
            buttonUpload.isEnabled = true

            timerViewer.cancel()
            timerView.text= null
            timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())  //save time for filename

            //uri
            val uriTxt = findViewById<View>(R.id.uriTxt) as TextView
            uriTxt.text =Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
        }*/

        // Ouvir gravação
        //var playAudio = MediaPlayer()

        buttonPlay.setOnClickListener{

            playAudio.reset()
            //buttonPlay.isEnabled = false
            playAudio.setDataSource(path)
            playAudio.prepare()
            playAudio.start()
           // }
        }

        buttonUpload.setOnClickListener(){

            uploadToStorage()

            //Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
        }

    }
/*
    fun onClickDwnl(view: View){
        //var path= Environment.getExternalStorageDirectory().toString()+"/recAndroid.mp3"
        //filename from the top
        var filename:String = "SOUND_"+timestamp+"_.mp3"

        // Create a storage reference from our app
        val storageRef = FirebaseStorage.getInstance().reference.child("Users/$id/Audios/$filename")
        var file = Uri.fromFile(File(path))
        storageRef.putFile(file)

    }*/

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

    private fun uploadToStorage(){
        buttonUpload.isEnabled = false

        var filename:String = "SOUND_"+timestamp+"_.mp3"

        // Create a storage reference from our app
        val storageRef = FirebaseStorage.getInstance().reference.child("Users/$id/Audios/$filename")
        var file = Uri.fromFile(File(path))
        var uploadTask: StorageTask<*>
        uploadTask = storageRef.putFile(file)

        //task complete
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {//put the storage photo link in the realtime database

                //download and play
                //var databaseRef = FirebaseDatabase.getInstance().reference.child("Users/$id/Audios")
                storageRef.downloadUrl.addOnSuccessListener { url ->//put the photo on the imageview
                    //url
                    val map = HashMap<String, Any>()
                    map["url"] = url.toString()
                    map["classValue"] = "Undefined"

                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child("$id")
                        .child("Audios")
                        .child("$timestamp")
                        .updateChildren(map)

                    // Só pra teste--------------------------------------------------------
                    val urlTxt = findViewById<View>(R.id.dwnTxt) as TextView
                    urlTxt.text = url.toString()

            /*//use this to play the audio file from firebase by accessing the child and getting the url string with a specific filename
                    //var file = Uri.fromFile(File(uri.toString()))
                    var playAudio = MediaPlayer()
                    playAudio.setDataSource(url.toString())
                    playAudio.prepare()
                    playAudio.start()*/

                    Toast.makeText(this, "Successfully Uploaded and downloaded", Toast.LENGTH_LONG).show()
                    sendNotification()
                }
            }else{
                Toast.makeText(this, "did nothing", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            buttonPlay.isEnabled = true
    }

    //notifications
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification title"
            val descriptionText = "Notification description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        val intent = Intent(this,RecordActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)
        //val bitmap: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ipca_logo)

        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.eec)
            .setContentTitle("titulo fixe")
            .setContentText("Audio uploaded to Firebase")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId,builder.build())
        }
    }
}

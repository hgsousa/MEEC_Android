package com.example.cm_meec_2021

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cm_meec_2021.R
import com.gauravk.audiovisualizer.visualizer.BarVisualizer


class RecordActivity : AppCompatActivity() {
    private lateinit var recordAudio : MediaRecorder
    lateinit var buttonStart:Button
    lateinit var buttonStop:Button
    lateinit var buttonPlay:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStop = findViewById<Button>(R.id.buttonStop)
        buttonPlay = findViewById<Button>(R.id.buttonPlay)

        var timerView = findViewById<TextView>(R.id.timerView)

        var path= Environment.getExternalStorageDirectory().toString()+"/recAndroid.3gp"

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

        val timerViewer = object: CountDownTimer(15000, 1000){
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
            recordAudio.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recordAudio.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            recordAudio.setMaxDuration(15000)  //Maximo de 15sec de gravação
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


        /*
        fun onClickButtonStart(view: View) {
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            mr.setOutputFile(path)
            mr.prepare()
            mr.start()
            buttonStop.isEnabled = true
            buttonStart.isEnabled = false
        }

        fun onClickButtonStop(view: View)
        {
            mr.stop()
            buttonStart.isEnabled = true
            buttonStop.isEnabled = false
        }

        fun onClickButtonPlay(view: View)
        {
            var mp = MediaPlayer()
            mp.setDataSource(path)
            mp.prepare()
            mp.start()
        }
        */
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            buttonPlay.isEnabled = true
    }
}

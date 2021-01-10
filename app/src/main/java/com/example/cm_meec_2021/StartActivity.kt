package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth


class StartActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        Handler().postDelayed({
            if(user != null){
                val intent = Intent(this, SecondActivity_login::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1500)
    }
}
package com.example.cm_meec_2021

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SecondActivity_login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_login)

        val email = intent.getStringExtra("email")
        val welcometv = findViewById<TextView>(R.id.secondActivityLogin)
        welcometv.text = "Hello " + email
    }
}
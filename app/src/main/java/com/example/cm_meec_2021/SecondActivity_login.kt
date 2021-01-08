package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class SecondActivity_login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_login)
        auth = FirebaseAuth.getInstance();

        //normal login
        val email = intent.getStringExtra("email_id")
        val name = intent.getStringExtra("user_id")
        val welcometv = findViewById<TextView>(R.id.secondActivity_name)
        welcometv.text = "Hello  :: $email \nuser :: $name"

        //google login
        val currentUser = auth.currentUser
        val emailgoogle = currentUser?.email
        val namegoogle = currentUser?.displayName
        welcometv.text = "Hello  :: $emailgoogle \nuser :: $namegoogle"

    }

    //Sign out
    fun onClickSignOutButton(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        //this 3 lines above can be substituted with onBackPressed() if we want to keep the info
        finish()
    }
}
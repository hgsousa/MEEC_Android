package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickLoginButton(view: View){
        val emailEditText = findViewById<EditText>(R.id.mainEmailAddress);
        val passwordEditText = findViewById<EditText>(R.id.mainPassword);

        val emailText = emailEditText.text.toString()
        val passwordText = passwordEditText.text.toString()

        if (!StringUtilis.validateEmail(emailText)) {
            Toast.makeText(this, "Email is not valid!", Toast.LENGTH_LONG).show()
            return
        }
        else if(!StringUtilis.validatePassword(passwordText)) {
            Toast.makeText(this, "Password is not valid!", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, SecondActivity_login::class.java)
        intent.putExtra("email", emailText)

        



        startActivity(intent)
    }

    fun onClickRegisterButton(view: View){
        val intent = Intent(this, ThirdActivity_register::class.java)
        startActivity(intent)
    }
}



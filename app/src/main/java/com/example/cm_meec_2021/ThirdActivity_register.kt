package com.example.cm_meec_2021

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class ThirdActivity_register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_register)
    }

    fun onClickRegisterButton(view: View){
        val nameEditText = findViewById<EditText>(R.id.RegisterNametag)
        val emailEditText = findViewById<EditText>(R.id.RegisterEmailAddress);
        val passwordEditText = findViewById<EditText>(R.id.RegisterPassword);
        val confirmedEditText = findViewById<EditText>(R.id.RegisterConfirmPassword);

        val nameText = nameEditText.text.toString()
        val emailText = emailEditText.text.toString()
        val passwordText = passwordEditText.text.toString()
        val comfirmedText = confirmedEditText.text.toString()

        if (!StringUtilis.validateEmail(emailText)) {
            Toast.makeText(this, "Email is not valid!", Toast.LENGTH_LONG).show()
            return
        }
        else if(!StringUtilis.validatePassword(passwordText)) {
            Toast.makeText(this, "Password is not valid!", Toast.LENGTH_LONG).show()
            return
        }
        else if(passwordText != comfirmedText) {
            Toast.makeText(this, "Password is not valid!", Toast.LENGTH_LONG).show()
            return
        }
    }
}
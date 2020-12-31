package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
    }

    fun onClickLoginButton(view: View){
        val emailEditText = findViewById<EditText>(R.id.mainEmailAddress);
        val passwordEditText = findViewById<EditText>(R.id.mainPassword);

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (!StringUtilis.validateEmail(email)) {
            Toast.makeText(this, "Email is not valid!", Toast.LENGTH_LONG).show()
            return
        }
        else if(!StringUtilis.validatePassword(password)) {
            Toast.makeText(this, "Password is not valid!", Toast.LENGTH_LONG).show()
            return
        }

        //after checking for a correct email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    //if registration successful
                    if(task.isSuccessful){

                        Toast.makeText(
                                this,
                                "Logged in Successfully",
                                Toast.LENGTH_SHORT
                        ).show()

                        //Send the user to the *main Activity* (SecondActivity_login) and close this one
                        val intent = Intent(this, SecondActivity_login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                        intent.putExtra("email_id", email)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        //if Logging in isn't successful show Error messages
                        Toast.makeText(
                                this,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }

    fun onClickRegisterButton(view: View){
        val intent = Intent(this, ThirdActivity_register::class.java)
        startActivity(intent)
    }

}
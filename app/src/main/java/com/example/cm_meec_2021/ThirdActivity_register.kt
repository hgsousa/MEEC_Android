package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ThirdActivity_register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_register)
        auth = FirebaseAuth.getInstance();

    }

    fun onClickRegisterButton(view: View) {
        val nameEditText = findViewById<EditText>(R.id.RegisterName)
        val emailEditText = findViewById<EditText>(R.id.RegisterEmailAddress);
        val passwordEditText = findViewById<EditText>(R.id.RegisterPassword);
        val confirmedEditText = findViewById<EditText>(R.id.RegisterConfirmPassword);

        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString().trim { it <= ' '}
        val password = passwordEditText.text.toString().trim { it <= ' '}
        val confPass = confirmedEditText.text.toString().trim { it <= ' '}

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name is not valid!", Toast.LENGTH_SHORT).show()
            return
        }else if (!StringUtilis.validateEmail(email)) {
            Toast.makeText(this, "Email is not valid!", Toast.LENGTH_SHORT).show()
            return
        } else if (!StringUtilis.validatePassword(password)) {
            Toast.makeText(this, "Password is to short!", Toast.LENGTH_SHORT).show()
            return
        } else if (password.toString() != confPass.toString()) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return
        } else {
            //after checking for a correct email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        //if registration successful
                        if(task.isSuccessful){
                            //Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            Toast.makeText(
                                    this,
                                    "Registered Successfully",
                                    Toast.LENGTH_SHORT
                            ).show()

                            /* --- o q nao entendo é como é q a data é guarda na conta do user e como a podemos ir buscar --- */

                            //Send the user to the *mainActivity* and close this one
                            val intent = Intent(this, SecondActivity_login::class.java)             //precisa de ser alterado pra enviar o user pro login e entao se ele quiser entrar no *mainactivity*
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("user_id", firebaseUser.uid)
                            intent.putExtra("email_id", email)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            //if registering isn't successful show Error messages
                            Toast.makeText(
                                    this,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
        }
    }
}
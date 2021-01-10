package com.example.cm_meec_2021

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SecondActivity_login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_login)

        auth = FirebaseAuth.getInstance();
        val currentUser = auth.currentUser

        //sign in with google
        val googleAuth=findViewById<TextView>(R.id.textView2)
        val id_goo = currentUser?.uid
        val name_goo = currentUser?.displayName
        val email_goo = currentUser?.email
        googleAuth.text = "user id :: $id_goo\n"+"email  :: $email_goo \n" + "name :: $name_goo";


        //sign in w email
        val email = intent.getStringExtra("email_id")
        val name = intent.getStringExtra("user_id")
        val welcometv = findViewById<TextView>(R.id.secondActivity_name)
        welcometv.text = "Hello  :: $email \nuser :: $name"
    }

    //Sign out
    fun onClickSignOutButton(view: View) {
        //Sign out google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        //Sign out email account
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        //this 3 lines above can be substituted with onBackPressed() if we want to keep the info
        finish()
    }
}
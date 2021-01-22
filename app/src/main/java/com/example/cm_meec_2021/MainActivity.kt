package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 120
    }
    private lateinit var auth: FirebaseAuth;
    private lateinit var googleSignInClient: GoogleSignInClient;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        //get email from register
        val emailReg = intent.getStringExtra("email_id")
    }

    //Sign in with Google
    fun onClickLoginGoogleButton(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInWithGoogle", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInWithGoogle", "Google sign in failed", e)
                }
            }else{
                //Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                Log.w("SignInWithGoogle", exception.toString())
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInWithGoogle", "signInWithCredential:success")
                    intent = Intent(this, SecondActivity_login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInWithGoogle", "signInWithCredential:failure", task.exception)
                    // ...
                }

                // ...
            }

    }

    //Sign in with email account
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
        }               //confirmar se está a funcionar (se der mal o q aconetece?)
        else {


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
                        intent.putExtra("user_id", auth.currentUser!!.uid) //mudei o FirebaseAuth.getInstance()
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
    }

    //Register with email account
    fun onClickRegisterButton(view: View){
        val intent = Intent(this, ThirdActivity_register::class.java)
        startActivity(intent)
    }

}
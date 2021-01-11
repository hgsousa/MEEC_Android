package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class ThirdActivity_register : AppCompatActivity() {
    companion object{
        private const val  RC_SIGN_IN = 120
    }

    private lateinit var auth: FirebaseAuth
    private  lateinit var googleLogInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_register)


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleLogInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance();

        /* erro registerImgbutton
        RegisterImgButton.setOnClickListener{
            signIn()
        }
         */
    }


    //Normal register
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
            auth.createUserWithEmailAndPassword(email, password)    //FirebaseAuth.getInstance()
                    .addOnCompleteListener { task ->

                        //if registration successful
                        if(task.isSuccessful){
                            //Firebase registered user
                            //val firebaseUser: FirebaseUser = task.result!!.user!!

                            Toast.makeText(
                                    this,
                                    "Registered Successfully",
                                    Toast.LENGTH_SHORT
                            ).show()

                            /* --- o q nao entendo é como é q a data é guarda na conta do user e como a podemos ir buscar --- */

                            //Send the user to the *mainActivity* and close this one
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            //intent.putExtra("user_id", firebaseUser.uid)
                            intent.putExtra("email_id", email)
                            startActivity(intent)
                            auth.signOut()      //need so that the user isn´t signed in when the account is created
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


    fun onClickRegisterImgButton(view: View) {
        signIn()
    }


    //login using google
    private fun signIn() {
        val signInIntent = googleLogInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("ThirdActivity_register", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("ThirdActivity_register", "Google sign in failed", e)
                    // ...
                }
            }else{
                Log.w("ThirdActivity_register", exception.toString())
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ThirdActivity_register", "signInWithCredential:success")
                    val intent = Intent(this,SecondActivity_login::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ThirdActivity_register", "signInWithCredential:failure", task.exception)

                }

            }
    }



}
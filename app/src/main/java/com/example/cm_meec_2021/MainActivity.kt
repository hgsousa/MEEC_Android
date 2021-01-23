package com.example.cm_meec_2021

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase  //var map = mutableMapOf<String,Any>()
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users")
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

//tentativa de ler os valores e colocálos nas text views ao abrir a activity
    /*    var updateEditTextString = findViewById<EditText>(R.id.update_editText).text.toString()
        var setEditTextString = findViewById<EditText>(R.id.set_edittext).text.toString()
        val id=auth.currentUser?.uid
        var map = mutableMapOf<String,Any>()
        if(map["name"]!=null||map["age"]!=null) {
            map["name"] = updateEditTextString
            map["age"] = setEditTextString

            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child("$id")
                .setValue(map)

            var readObserve_editText = this.findViewById<TextView>(R.id.read_observe_textView)
            //val id=auth.currentUser?.uid
            database.reference
                .child("Users")
                .child("$id")
                .addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        var map = p0.value as Map<String,Any>
                        if(map!=null) {
                            readObserve_editText.text = map["age"].toString()
                        }else{
                            Log.d("teste", "no user found")
                        }

                    }
                })
        }
        */
        var readSingle_editText = findViewById<TextView>(R.id.read_single_textView)
        val id=auth.currentUser?.uid
        database.reference
            .child("Users")
            //.child("$id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }
                override fun onDataChange(p0: DataSnapshot) {

                    var map = p0.value as Map<String,Any>
                    readSingle_editText.text = map["age"].toString()

                }
            })
    }


    fun onClickRecordActivity (view: View){
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }


    //Sign out
    fun onClickSignOutButton(view: View) {
        //Sign out google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        //Sign out facebook
        LoginManager.getInstance().logOut()

        //Sign out email account
        auth.signOut()
        val intent = Intent(this, SecondActivity_login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        //this 3 lines above can be substituted with onBackPressed() if we want to keep the info
        finish()

    }

    //real time database
    fun onClickButtonSet(view:View){
        var updateEditTextString = findViewById<EditText>(R.id.update_editText).text.toString()
        var setEditTextString = findViewById<EditText>(R.id.set_edittext).text.toString()
        val id=auth.currentUser?.uid
        var map = mutableMapOf<String,Any>()
        map["name"] = updateEditTextString
        map["age"] = setEditTextString

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("$id")
            .setValue(map)
    }

    fun onClickButtonUpdate(view: View){
        var updateEditTextString = findViewById<EditText>(R.id.update_editText).text.toString()
        var setEditTextString = findViewById<EditText>(R.id.set_edittext).text.toString()
        val id=auth.currentUser?.uid
        var map = mutableMapOf<String,Any>()
        map["name"] = updateEditTextString
        map["age"] = setEditTextString

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("$id")
            .updateChildren(map)
    }

    fun onClickButtonDelete(view: View){
        val id=auth.currentUser?.uid
        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("$id")
            .child("name")
            .removeValue()
    }

    fun onClickButtonReadSingle(view: View){
        var readSingle_editText = findViewById<TextView>(R.id.read_single_textView)
        val id=auth.currentUser?.uid
        database.reference
            .child("Users")
            .child("$id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    var map = p0.value as Map<String,Any>
                    readSingle_editText.text = map["age"].toString()

                }
            })
    }

    fun onClickButtonReadObserve(view: View){
        var readObserve_editText = this.findViewById<TextView>(R.id.read_observe_textView)
        val id=auth.currentUser?.uid
        database.reference
            .child("Users")
            .child("$id")
            .addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    var map = p0.value as Map<String,Any>
                    readObserve_editText.text = map["age"].toString()

                }
            })
    }

}
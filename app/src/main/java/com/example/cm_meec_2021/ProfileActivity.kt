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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase  //var map = mutableMapOf<String,Any>()
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile"

        database = FirebaseDatabase.getInstance()
        //reference = database.getReference("Users")  //unnecessary i think
        auth = FirebaseAuth.getInstance();
        val currentUser = auth.currentUser

        val emailText = findViewById<TextView>(R.id.emailText)
        val nameText = findViewById<TextView>(R.id.nameText)
        emailText.text = currentUser?.email
        nameText.text = currentUser?.displayName

        /*//-----------------------------------------------------------JUST FOR TESTING------------
        val id_goo = currentUser?.uid
        emailText.text = currentUser?.displayName + "\nuser id :: $id_goo\n"+"email  :: ${emailText.text} \n" + "name :: ${nameText.hint}";
        *///---------------------------------------------------------------------------------------

        var phoneText = findViewById<EditText>(R.id.phoneText)
        var companyIdText = findViewById<EditText>(R.id.companyIdText)
        var companyText = findViewById<EditText>(R.id.companyText)

        val id=auth.currentUser?.uid
        var info = database.reference.child("Users").child("$id").child("Info")

                info.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(p0: DataSnapshot) {

                        if(p0.value != null) {  //if(Info != null)

                            var map = p0.value as Map<String, Any>
                            check4null(companyIdText, map, "companyId")
                            check4null(companyText, map, "company")
                            check4null(phoneText, map, "phone")

                        }
                    }
                })
    }

    private fun check4null( place:EditText,  map: Map<String, Any>, key:String){
        if( map[key]!=null){
            place.hint = map[key].toString()
        }
        //else keeps the original hint
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
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        //this 3 lines above can be substituted with onBackPressed() if we want to keep the info
        finish()

    }

    //get Info from database

    //if the user rights something it saves it in the map, if the user doesnt leaves the textbox blank it doesnt update that map[key]
    private fun check4blank( map: MutableMap<String, Any>, key:String, info:String){
        if( info.trim { it <= ' '} != ""){
            map[key] = info
        }
    }

    fun onClickButtonUpdate(view: View){
        var phoneText = findViewById<EditText>(R.id.phoneText).text.toString()
        var companyIdText = findViewById<EditText>(R.id.companyIdText).text.toString()
        var companyText = findViewById<EditText>(R.id.companyText).text.toString()
        val id=auth.currentUser?.uid
        var map = mutableMapOf<String,Any>()
        /*if(phone!=null){
            map["phone"] = phone
        }
        map["companyId"] = companyId
        map["company"] = company*/

        check4blank(map, "phone", phoneText)
        check4blank(map, "companyId", companyIdText)
        check4blank(map, "company", companyText)

        //usar ainda phone number genero

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("$id")
            .child("Info")
            .updateChildren(map)

        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
    }
}
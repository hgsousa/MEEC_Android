package com.example.cm_meec_2021

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase  //var map = mutableMapOf<String,Any>()
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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


        // Get user Info
     /*   var updateEditTextString = findViewById<EditText>(R.id.nameText).text.toString()
        var setEditTextString = findViewById<EditText>(R.id.ageText).text.toString()
        val id=auth.currentUser?.uid
        var map = mutableMapOf<String,Any>()
        map["name"] = updateEditTextString //name da firebase
        map["age"] = setEditTextString
        map["company"] = "IPCA"
        //usar ainda phone number genero

        FirebaseDatabase.getInstance().reference
                .child("Users")
                .child("$id")
                .child("Info")
                .updateChildren(map)*/

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
                        /*else{ //creates the folder Info
                            println("--------------NAAAAAAAAAAAO------------")
                            var map = mutableMapOf<String,Any>()
                            map["phone"] = ""
                            map["companyId"] = ""
                            map["company"] = ""

                            FirebaseDatabase.getInstance().reference
                                    .child("Users")
                                    .child("$id")
                                    .child("Info")
                                    .updateChildren(map)
                        }*/
                    }
                })

    }

    private fun check4null( place:EditText,  map: Map<String, Any>, key:String){
        if( map[key]!=null){
            place.hint = map[key].toString()
        }
        //else keeps the original hint
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
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        //this 3 lines above can be substituted with onBackPressed() if we want to keep the info
        finish()

    }

    //------------real time database--------------
  /*  fun onClickButtonSet(view:View){
        var updateEditTextString = findViewById<EditText>(R.id.nameText).text.toString()
        var setEditTextString = findViewById<EditText>(R.id.ageText).text.toString()
        val id=auth.currentUser?.uid
        var map = mutableMapOf<String,Any>()
        map["name"] = updateEditTextString
        map["age"] = setEditTextString

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("$id")
            .setValue(map)
    }*/

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
    }

    fun onClickButtonDelete(view: View){
        val id=auth.currentUser?.uid
        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child("$id")
            .child("name")  //alterar
            .removeValue()
    }
/*
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
        var readObserve_editText = findViewById<TextView>(R.id.read_observe_textView)
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
*/
}
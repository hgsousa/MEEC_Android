package com.example.cm_meec_2021

import android.app.ActivityManager
import android.app.PictureInPictureParams
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//import com.example.cm_meec_2021.databinding.ActivityMainBinding                   //has giving an Error here but not before merge (as it was not used)
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable
import java.text.SimpleDateFormat
import java.util.*

class AudioListActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    lateinit var rv_recyclerView:RecyclerView
    lateinit var nameProfile_textView:TextView
    lateinit var swipe: SwipeRefreshLayout
    lateinit var fabRecord: FloatingActionButtonExpandable

    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    lateinit var profile_cardview: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_list)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance();
        val currentUser = auth.currentUser

        nameProfile_textView = findViewById(R.id.nameProfile_textView)
        profile_cardview = findViewById(R.id.profile_cardview)

        val nameGoogle = currentUser?.displayName
        nameProfile_textView.text = "Welcome, ${nameGoogle}"

        /*val nameEmail = intent.getStringExtra("email_id")

        if (nameGoogle == null ){
            nameProfile_textView.text = "Welcome, ${nameEmail}"
        }else{
            nameProfile_textView.text = "Welcome, ${nameGoogle}"
        }*/

        rv_recyclerView = findViewById<RecyclerView>(R.id.rv_recyclerView)
        //refreshApp()
        postToList()

        var buttonRefresh = findViewById<ImageButton>(R.id.button2)
        buttonRefresh.setOnClickListener(){
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);

        }

        rv_recyclerView.layoutManager = LinearLayoutManager(this)
        rv_recyclerView.adapter = RecyclerAdapter(titlesList,descList,imagesList)

        setUpFab()
        profile_cardview.setOnClickListener(){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            //finish()
        }
    }

    private fun  setUpFab(){
        fabRecord = findViewById(R.id.floatingActionButton)
        rv_recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    fabRecord.collapse()
                } else {
                    fabRecord.expand()
                }
            }
        })
        fabRecord.setOnClickListener{
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun refreshApp(){
        swipe = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipe.setOnClickListener{
            Toast.makeText(this,"List Refreshed", Toast.LENGTH_LONG).show()
            swipe.isRefreshing = false
        }
    }

    private fun addtoList(title: String,description: String,Image: Int){
        titlesList.add(title)
        descList.add(description)
        imagesList.add(Image)
    }

    private fun postToList(){
        val id = FirebaseAuth.getInstance().currentUser?.uid
        val dataRef = FirebaseDatabase.getInstance().reference
        dataRef.child("Users/$id/Audios/")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for ((counter,i) in p0.children.withIndex()){
                        val filename = i.key.toString()
                        addtoList("Audio ${counter+1}","$filename", R.mipmap.ic_launcher_sound)
                        rv_recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            })
    }

}

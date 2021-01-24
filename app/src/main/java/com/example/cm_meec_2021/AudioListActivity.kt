package com.example.cm_meec_2021

import android.app.ActivityManager
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cm_meec_2021.databinding.ActivityMainBinding
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
    lateinit var rv_recyclerView:RecyclerView

    lateinit var swipe: SwipeRefreshLayout
    lateinit var fabRecord: FloatingActionButtonExpandable

    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_list)

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
        val storageRef = FirebaseDatabase.getInstance().reference
        storageRef.child("Users/$id/Audios/")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for ((counter,i) in p0.children.withIndex()){
                        val filename = i.key.toString()
                        addtoList("Audio ${counter+1}"," $filename", R.mipmap.ic_launcher_sound)
                    }
                }
            })
    }

}

package com.example.cm_meec_2021

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.TextView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.title = "About Us"

        // About Us Title
        val pageTitle1 = findViewById<TextView>(R.id.textView4)
        pageTitle1.text = "Trabalho de Computação Móvel\n "

        // Authors
        val pageTitle2 = findViewById<TextView>(R.id.textView5)
        pageTitle2.text = "Authors: \n" +
                            "Hugo Sousa 13297 \n" +
                            "Diogo Vale 14810 \n" +
                            "Paulo Costa 17987 \n"

        // About intructions
        val pageTitle3 = findViewById<TextView>(R.id.textView6)
        pageTitle3.text = "The purpose of this application is to record 10-second audio clips and store them in a remote database.\n\n" +
                "So that it can be accessed by other platforms and later detection of faults in a system. After assuming the flagged, " +
        "letting the user know if the file in question is from an OK or NOK machine.\n\n" +
                "This mobile application located and registered through the Firebase platform using email + password, Facebook" +
                " or Google account. \n\n" +
                "This authentication allows each user to have their files. These are corrected by the submission history and " +
                " can later be consulted and reproduced on the mobile phone.\n"







    }
    //Back button

    fun onClickBack(view: View) { val intent = Intent(this , LoginActivity::class.java)
        startActivity(intent)
        finish()}


}
package com.example.cm_meec_2021

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database (context: Context):SQLiteOpenHelper(context, dbname, factory, version){
    override fun onCreate(p0: SQLiteDatabase?) {

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }


    companion object {
        internal val dbname = "userdb"
        internal val factory = null
        internal val version = 1
    }


}
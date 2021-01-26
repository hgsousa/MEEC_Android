package com.example.cm_meec_2021

import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter(private var titles: List<String>, private var details: List<String>, private var images:List<Int>):
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val itemTitle: TextView = itemView.findViewById(R.id.tv_title)
        val itemDetails: TextView = itemView.findViewById(R.id.tv_descrition)
        val itemPicture: ImageView = itemView.findViewById(R.id.iv_image)

        init {
            itemView.setOnClickListener{v:View->
                val position:Int = adapterPosition
                Toast.makeText(itemView.context,"You click on an item # ${position + 1}", Toast.LENGTH_SHORT).show()

                Handler().postDelayed({
                    val intent = Intent(itemView.context, PlayAudioActivity::class.java) //create an intent for the activity you want
                    val audioPosition = position + 1
                    intent.putExtra("audioNumber",audioPosition.toString()) //sends audio list number
                    intent.putExtra("filename", "${details[position]}") //sends audio filename (realtime database key for audio URL)
                    startActivity(itemView.context, intent,null) //start the activity
                    //(this as AudioListActivity).finish()

                }, 1000)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout,parent,false)
        return  ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = titles[position]
        holder.itemDetails.text = details[position]
        holder.itemPicture.setImageResource(images[position])
    }
}

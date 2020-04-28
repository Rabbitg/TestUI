package com.hour.uigithub

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteViewHolder(view:View) : RecyclerView.ViewHolder(view){
    var title: TextView = view.findViewById(R.id.tvTitle)
    var content: TextView = view.findViewById(R.id.tvContent)
    var edit: ImageView = view.findViewById(R.id.ivEdit)
    var delete: ImageView = view.findViewById(R.id.ivDelete)

}
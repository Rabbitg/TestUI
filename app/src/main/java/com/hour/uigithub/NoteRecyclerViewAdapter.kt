package com.hour.uigithub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hour.uigithub.database.Note
import com.hour.uigithub.goalMain.TimerActivity
import kotlinx.android.synthetic.main.item_note.view.*
import java.io.Serializable

class NoteRecyclerViewAdapter(
    private val notesList: MutableList<Note>,
    private val context: Context,
    private val firestoreDB: FirebaseFirestore
)
    : RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder>(){

    companion object{
        val TAG = "RecyclerView"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.title.text = note.title
        holder.content.text=note.content
        holder.edit.setOnClickListener { updateNote(note) }
        holder.delete.setOnClickListener { deleteNote(note.id!!, position) }
        holder.check.setOnCheckedChangeListener {_, isChecked->
            if(isChecked){
                deleteNote(note.id!!, position)
            }
        }
        holder.itemView.setOnClickListener {
                v: View -> Unit
            Log.d(TAG, "onItemClick for position: " + holder.adapterPosition)
            val intent = Intent(context, TimerActivity::class.java)
            intent.putExtra("id",notesList.get(holder.adapterPosition).id)
            intent.putExtra("title",notesList.get(holder.adapterPosition).title)
            intent.putExtra("content",notesList.get(holder.adapterPosition).content)

            context.startActivity(intent)
        }


    }

    inner class ViewHolder internal constructor(view:View) : RecyclerView.ViewHolder(view){
        internal var title: TextView = view.findViewById(R.id.tvTitle)
        internal var content: TextView = view.findViewById(R.id.tvContent)
        internal var edit: ImageView = view.findViewById(R.id.ivEdit)
        internal var delete: ImageView = view.findViewById(R.id.ivDelete)
        internal var check: CheckBox = view.findViewById(R.id.chk_box)

    }

    private fun updateNote(note:Note){
        val intent = Intent(context,NoteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateNoteId", note.id)
        intent.putExtra("UpdateNoteTitle", note.title)
        intent.putExtra("UpdateNoteContent", note.content)
        context.startActivity(intent)
    }

    private fun deleteNote(id:String,position: Int){
        firestoreDB.collection("add_notes")
            .document(id)
            .delete()
            .addOnCompleteListener{
                notesList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,notesList.size)
                Toast.makeText(context,"Note has been deleted!", Toast.LENGTH_SHORT).show()
            }
    }


}
package com.hour.uigithub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.hour.uigithub.database.Note
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {

    private val TAG = "AddNoteActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateNoteId").toString()

            edtTitle.setText(bundle.getString("UpdateNoteTitle"))
            edtContent.setText(bundle.getString("UpdateNoteContent"))
        }

        btAdd.setOnClickListener {
            val title = edtTitle.text.toString()
            val content = edtContent.text.toString()

            if (title.isNotEmpty()) {
                if (id.isNotEmpty()) {
                    updateNote(id, title, content)
                } else {
                    addNote(title, content)
                }
            }
            finish()
        }
    }

    private fun updateNote(id: String, title: String, content: String) {
        val note = Note(id, title, content).toMap()

        firestoreDB!!.collection("add_notes")
            .document(id)
            .set(note)
            .addOnSuccessListener {
                Log.e(TAG, "Note document update successful!")
                Toast.makeText(applicationContext, "Note has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Note could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addNote(title: String, content: String) {
        val note = Note(title, content).toMap()

        firestoreDB!!.collection("add_notes")
            .add(note)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Note has been added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Note could not be added!", Toast.LENGTH_SHORT).show()
            }
    }
}

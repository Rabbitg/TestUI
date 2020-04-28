package com.hour.uigithub.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hour.uigithub.NoteActivity
import com.hour.uigithub.NoteRecyclerViewAdapter
import com.hour.uigithub.NoteViewHolder
import com.hour.uigithub.R
import com.hour.uigithub.database.Note
import com.hour.uigithub.util.logout
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(),View.OnClickListener {

    private val TAG = "HomeActivity"

    private var mAdapter: NoteRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        firestoreDB = FirebaseFirestore.getInstance()

        loadNotesList()
        firestoreListener = firestoreDB!!.collection("notes")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val notesList = mutableListOf<Note>()

                for (doc in documentSnapshots!!) {
                    val note = doc.toObject(Note::class.java)
                    note.id = doc.id
                    notesList.add(note)
                }

                mAdapter = NoteRecyclerViewAdapter(notesList, applicationContext, firestoreDB!!)
                rvNoteList.adapter = mAdapter
            })
        val navController = Navigation.findNavController(this,
            R.id.fragment
        )
        NavigationUI.setupWithNavController(nav_view, navController)
        NavigationUI.setupActionBarWithNavController(
            this,
            navController, drawer_layout
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener!!.remove()
    }

    private fun loadNotesList() {
        firestoreDB!!.collection("notes")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val notesList = mutableListOf<Note>()

                    for (doc in task.result!!) {
                        val note = doc.toObject<Note>(Note::class.java)
                        note.id = doc.id
                        notesList.add(note)
                    }

                    mAdapter = NoteRecyclerViewAdapter(notesList, applicationContext, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    rvNoteList.layoutManager = mLayoutManager
                    rvNoteList.itemAnimator = DefaultItemAnimator()
                    rvNoteList.adapter = mAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment),
            drawer_layout
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_logout) {

            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    FirebaseAuth.getInstance().signOut()
                    logout()

                }
                setNegativeButton("Cancel") { _, _ ->
                }
            }.create().show()

        }
        if (item != null){
            if(item.itemId == R.id.addNote){
                val intent = Intent(this,NoteActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {

    }


}

package com.hour.uigithub.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hour.uigithub.NoteRecyclerViewAdapter
import com.hour.uigithub.R
import com.hour.uigithub.database.Note
import com.hour.uigithub.goalMain.TimerActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private var mAdapter:NoteRecyclerViewAdapter? = null

    private var firestoreDB : FirebaseFirestore?= null
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        floatingActionButton.setOnClickListener {
            val intent = Intent (activity, TimerActivity::class.java)
            activity?.startActivity(intent)
        }

        firestoreDB = FirebaseFirestore.getInstance()

        loadNotesList()

        firestoreListener = firestoreDB!!.collection("add_notes")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if(e != null){
                    Log.e(TAG,"Listen failed!",e)
                    return@EventListener
                }
                val notesList = mutableListOf<Note>()

                for(doc in documentSnapshots!!){
                    val note = doc.toObject(Note::class.java)
                    note.id = doc.id
                    notesList.add(note)
                }
                mAdapter = NoteRecyclerViewAdapter(notesList,requireContext(),firestoreDB!!)
                rvNoteList2.adapter = mAdapter
            })

    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener!!.remove()
    }

    private fun loadNotesList(){
        firestoreDB!!.collection("add_notes")
            .get()
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    val noteList = mutableListOf<Note>()
                    for (doc in task.result!!){
                        val note = doc.toObject(Note::class.java)
                        note.id = doc.id
                        noteList.add(note)
                    }
                    mAdapter = NoteRecyclerViewAdapter(noteList,requireContext(),firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(requireContext())
                    rvNoteList2.layoutManager = mLayoutManager
                    //item들의 추가/삭제/이동 이벤트에 대한
                    // 기본적인 animation을 제공하는 클래스이다.
                    rvNoteList2.itemAnimator = DefaultItemAnimator()
                    rvNoteList2.adapter = mAdapter
                } else{
                    Log.d(TAG, "Error getting documents: ",task.exception)
                }
            }
    }


}

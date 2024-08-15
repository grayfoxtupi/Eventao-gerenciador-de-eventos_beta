package com.example.app3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User

class ParticipantsActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerViewParticipants: RecyclerView
    private lateinit var participantsList: MutableList<User>
    private lateinit var participantAdapter: ParticipantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participants)

        val eventId = intent.getStringExtra("eventId") ?: ""

        recyclerViewParticipants = findViewById(R.id.recyclerViewParticipants)
        recyclerViewParticipants.layoutManager = LinearLayoutManager(this)
        participantsList = mutableListOf()

        participantAdapter = ParticipantAdapter(this, participantsList)
        recyclerViewParticipants.adapter = participantAdapter

        database = FirebaseDatabase.getInstance().getReference("event_registrations").child(eventId)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                participantsList.clear()
                for (participantSnapshot in snapshot.children) {
                    val user = participantSnapshot.getValue(User::class.java)
                    if (user != null) {
                        participantsList.add(user)
                    }
                }
                participantAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
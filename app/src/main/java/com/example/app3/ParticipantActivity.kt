package com.example.app3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ParticipantActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var participantAdapter: ParticipantAdapter
    private lateinit var participantList: MutableList<User>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participants)

        val toolbar = findViewById<Toolbar>(R.id.header)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.back_arrow)
        }

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewParticipants)
        recyclerView.layoutManager = LinearLayoutManager(this)

        participantList = mutableListOf()
        participantAdapter = ParticipantAdapter(participantList)
        recyclerView.adapter = participantAdapter

        database = FirebaseDatabase.getInstance().reference.child("event_participants")

        fetchParticipants()
    }

    private fun fetchParticipants() {
        val eventId = intent.getStringExtra("EVENT_ID")
        if (eventId != null) {
            database.child(eventId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    participantList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            participantList.add(user)
                        }
                    }
                    participantAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }
    }

    data class User(
        val id: String = "",
        val name: String = "",
        val email: String = "",
        val cpf: String = "",
        val status: String = ""
    )
}

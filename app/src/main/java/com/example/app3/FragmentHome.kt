
package com.example.app3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FragmentHome : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerViewEvents: RecyclerView
    private lateinit var eventsList: MutableList<Event>
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val view = inflater.inflate(R.layout.activity_events, container, false)

        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents)
        recyclerViewEvents.layoutManager = GridLayoutManager(context, 1)
        eventsList = mutableListOf()

        eventAdapter = EventAdapter(requireContext(), eventsList)
        recyclerViewEvents.adapter = eventAdapter

        database = FirebaseDatabase.getInstance().getReference("events")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsList.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)

                    if (event != null) {
                        eventsList.add(event)
                    }
                }
                eventAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        return view
    }
}

package com.example.app3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FragmentCreateEvent : Fragment() {

    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextEventName: EditText = view.findViewById(R.id.editTextEventName)
        val editTextEventDescription: EditText = view.findViewById(R.id.editTextEventDescription)
        val editTextEventDate: EditText = view.findViewById(R.id.editTextEventDate)
        val editTextEventImageUrl: EditText = view.findViewById(R.id.editTextEventImageUrl)
        val editTextEventHour: EditText = view.findViewById(R.id.editTextHour)
        val editTextEventLocalName: EditText = view.findViewById(R.id.editTextLocalName)
        val editTextEventStreetNumber: EditText = view.findViewById(R.id.editTextEventStreetNumber)
        val editTextEventCityState: EditText = view.findViewById(R.id.editTextCityState)
        val buttonAddEvent: Button = view.findViewById(R.id.buttonAddEvent)


        database = FirebaseDatabase.getInstance().getReference("events")

        buttonAddEvent.setOnClickListener {
            val eventName = editTextEventName.text.toString()
            val eventDescription = editTextEventDescription.text.toString()
            val eventDate = editTextEventDate.text.toString()
            val eventImageUrl = editTextEventImageUrl.text.toString()
            val eventHour = editTextEventHour.text.toString()
            val eventLocalName = editTextEventLocalName.text.toString()
            val eventStreetNumber = editTextEventStreetNumber.text.toString()
            val eventCityState = editTextEventCityState.text.toString()

            if (eventName.isNotEmpty() && eventDescription.isNotEmpty() && eventDate.isNotEmpty() && eventImageUrl.isNotEmpty() &&
                eventHour.isNotEmpty() && eventLocalName.isNotEmpty() && eventStreetNumber.isNotEmpty() && eventCityState.isNotEmpty()) {
                getNextEventName { eventId ->
                    if (eventId != null) {
                        val event = Event(
                            id = eventId.hashCode(),
                            name = eventName,
                            description = eventDescription,
                            date = eventDate,
                            imageUrl = eventImageUrl,
                            hour = eventHour,
                            localName = eventLocalName,
                            streetNumber = eventStreetNumber,
                            cityState = eventCityState
                        )

                        database.child(eventId).setValue(event).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show()
                                // Navigate back or clear fields
                                requireActivity().supportFragmentManager.popBackStack()
                            } else {
                                Toast.makeText(requireContext(), "Failed to add event", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to generate event ID", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getNextEventName(callback: (String?) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var maxNumber = 0
                for (child in snapshot.children) {
                    val key = child.key
                    if (key != null && key.startsWith("event")) {
                        val number = key.removePrefix("event").toIntOrNull()
                        if (number != null && number > maxNumber) {
                            maxNumber = number
                        }
                    }
                }
                val nextNumber = maxNumber + 1
                callback("event$nextNumber")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}

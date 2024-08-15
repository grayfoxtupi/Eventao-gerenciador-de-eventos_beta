package com.example.app3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventDetail : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val textViewName = findViewById<TextView>(R.id.textViewName)
        val textViewDescription = findViewById<TextView>(R.id.textViewDescription)
        val textViewDate = findViewById<TextView>(R.id.textViewDate)
        val textViewHour = findViewById<TextView>(R.id.textViewHour) // Novo campo
        val textViewLocalName = findViewById<TextView>(R.id.textViewLocalName) // Novo campo
        val textViewLocalStreetNumber = findViewById<TextView>(R.id.textViewLocalStreetNumber) // Novo campo
        val textViewCityState = findViewById<TextView>(R.id.textViewCityState) // Novo campo
        val imageViewEvent = findViewById<ImageView>(R.id.imageViewEvent)
        val buttonParticipants = findViewById<Button>(R.id.buttonParticipants)
        val buttonEditEvent = findViewById<Button>(R.id.buttonEditEvent)
        val buttonSubscribe = findViewById<Button>(R.id.buttonSubscribe)
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

        fun onBackPressed() {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }

        val eventId = intent.getStringExtra("EVENT_ID")
        val eventName = intent.getStringExtra("EVENT_NAME")
        val eventDescription = intent.getStringExtra("EVENT_DESCRIPTION")
        val eventDate = intent.getStringExtra("EVENT_DATE")
        val eventHour = intent.getStringExtra("EVENT_HOUR") // Novo campo
        val eventLocalName = intent.getStringExtra("EVENT_LOCAL_NAME") // Novo campo
        val eventStreetNumber = intent.getStringExtra("EVENT_STREET_NUMBER") // Novo campo
        val eventCityState = intent.getStringExtra("EVENT_CITY_STATE") // Novo campo
        val eventImageUrl = intent.getStringExtra("EVENT_IMAGE_URL")

        Log.d("EventDetail", "Received eventId: $eventId")
        Log.d("EventDetail", "Received eventName: $eventName")
        Log.d("EventDetail", "Received eventDescription: $eventDescription")
        Log.d("EventDetail", "Received eventDate: $eventDate")
        Log.d("EventDetail", "Received eventHour: $eventHour")
        Log.d("EventDetail", "Received eventLocalName: $eventLocalName")
        Log.d("EventDetail", "Received eventStreetNumber: $eventStreetNumber")
        Log.d("EventDetail", "Received eventCityState: $eventCityState")
        Log.d("EventDetail", "Received eventImageUrl: $eventImageUrl")

        textViewName.text = eventName
        textViewDescription.text = eventDescription
        textViewDate.text = eventDate
        textViewHour.text = eventHour // Novo campo
        textViewLocalName.text = eventLocalName // Novo campo
        textViewLocalStreetNumber.text = eventStreetNumber // Novo campo
        textViewCityState.text = eventCityState // Novo campo

        // Carregar a imagem do evento na ImageView usando Glide
        if (!eventImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(eventImageUrl)
                .into(imageViewEvent)
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        buttonParticipants.setOnClickListener {
            val intent = Intent(this, ParticipantActivity::class.java)
            intent.putExtra("EVENT_ID", eventId)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        buttonEditEvent.setOnClickListener {
            val intent = Intent(this, EditEvent::class.java)
            intent.putExtra("EVENT_ID", eventId)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }


//        buttonSubscribe.setOnClickListener {
//            Log.d("EventDetail", "Button Subscribe clicked")
//            val user = auth.currentUser
//            if (user != null) {
//                Log.d("EventDetail", "User is not null: ${user.email}")
//            } else {
//                Log.d("EventDetail", "User is null")
//            }
//            if (eventId != null) {
//                Log.d("EventDetail", "EventId is not null: $eventId")
//            } else {
//                Log.d("EventDetail", "EventId is null")
//            }
//            if (user != null && eventId != null) {
//                Log.d("EventDetail", "User and eventId are not null")
//                val userEmail = user.email
//                if (userEmail != null) {
//                    subscribeToEvent(eventId, userEmail)
//                    val intent = Intent(this, EventDetailClientQRcode::class.java)
//                    intent.putExtra("USER_EMAIL", userEmail)
//                    intent.putExtra("EVENT_ID", eventId)
//                    Log.d("EventDetail", "Starting QrCodeActivity with email: $userEmail")
//                    startActivity(intent)
//                } else {
//                    Log.d("EventDetail", "User email is null")
//                }
//            } else {
//                Log.d("EventDetail", "User or eventId is null")
//            }
//        }

        buttonSubscribe.setOnClickListener {
            val user = auth.currentUser
            if (user != null && eventId != null) {
                val userEmail = user.email
                if (userEmail != null) {
                    subscribeToEvent(eventId, userEmail)
                    val intent = Intent(this, EventDetailClientQRcode::class.java)
                    intent.putExtra("USER_EMAIL", userEmail)
                    intent.putExtra("EVENT_ID", eventId)
                    intent.putExtra("EVENT_NAME", eventName)
                    intent.putExtra("EVENT_DATE", eventDate)
                    intent.putExtra("EVENT_HOUR", eventHour)
                    intent.putExtra("EVENT_LOCAL_NAME", eventLocalName)
                    intent.putExtra("EVENT_STREET_NUMBER", eventStreetNumber)
                    intent.putExtra("EVENT_CITY_STATE", eventCityState)
                    intent.putExtra("EVENT_IMAGE_URL", eventImageUrl)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                } else {
                    Log.d("EventDetail", "User email is null")
                }
            } else {
                Log.d("EventDetail", "User or eventId is null")
            }
        }




    }

    private fun subscribeToEvent(eventId: String, userEmail: String) {
        val eventSubscription = hashMapOf(
            "eventId" to eventId,
            "userEmail" to userEmail
        )
        db.collection("subscriptions")
            .add(eventSubscription)
            .addOnSuccessListener { documentReference ->
                Log.d("EventDetail", "Subscription successfully written")
            }
            .addOnFailureListener { e ->
                Log.w("EventDetail", "Error adding document", e)
            }
    }
}

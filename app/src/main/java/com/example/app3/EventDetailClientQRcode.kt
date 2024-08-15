package com.example.app3

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

class EventDetailClientQRcode : AppCompatActivity() {

    private lateinit var imageViewEvent: ImageView
    private lateinit var textViewName: TextView
    private lateinit var imageViewQRCodeEvent: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail_client_qrcode)

        imageViewEvent = findViewById(R.id.imageViewEvent)
        textViewName = findViewById(R.id.textViewName)
        imageViewQRCodeEvent = findViewById(R.id.imageViewQRCodeEvent)


        val toolbar = findViewById<Toolbar>(R.id.header)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.back_arrow)
        }

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, EventDetail::class.java)
            startActivity(intent)
            finish()
        }

        fun onBackPressed() {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }



        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        eventId = intent.getStringExtra("EVENT_ID")

        val eventName = intent.getStringExtra("EVENT_NAME")
        val eventImageUrl = intent.getStringExtra("EVENT_IMAGE_URL")

        textViewName.text = eventName

        // Carrega a imagem do evento no ImageView usando Glide
        if (!eventImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(eventImageUrl)
                .into(imageViewEvent)
        }

        // Gera o QR Code
        generateQRCode(auth.currentUser?.email, eventId)
    }

    private fun generateQRCode(userEmail: String?, eventId: String?) {
        if (userEmail != null && eventId != null) {
            try {
                val text = "User: $userEmail, Event ID: $eventId"
                val size = 500 // Define o tamanho do QR Code (largura e altura)

                val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
                val bitmap = createBitmap(bitMatrix)

                // Exibe o QR Code no ImageView
                imageViewQRCodeEvent.setImageBitmap(bitmap)

                // Após a geração bem-sucedida do QR Code, envie a descrição do usuário para o evento
                updateUserEventParticipation(userEmail, eventId)
            } catch (e: WriterException) {
                Log.e("EventDetailClientQRcode", "Error generating QR code", e)
            }
        } else {
            Log.e("EventDetailClientQRcode", "User email or event ID is null")
        }
    }

    private fun createBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    private fun updateUserEventParticipation(userEmail: String, eventId: String) {
        // Atualiza os dados no Realtime Database
        val userRef = database.getReference("event_participants").child(eventId)
            .child(userEmail.replace(".", ","))
        val user = User(email = userEmail, id = eventId)
        userRef.setValue(user)
            .addOnSuccessListener {
                Log.d("EventDetailClientQRcode", "User participation updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("EventDetailClientQRcode", "Error updating user participation", e)
            }
    }

    data class User(
        val email: String = "",
        val id: String = ""
    )
}

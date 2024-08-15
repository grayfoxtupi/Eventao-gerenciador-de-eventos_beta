package com.example.app3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCodeReaderActivity : AppCompatActivity() {

    private var currentDialog: AlertDialog? = null
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_reader)

        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Inicia a atividade EventsActivity ao pressionar o botão de voltar
                val intent = Intent(this@QRCodeReaderActivity, EventsActivity::class.java)
                startActivity(intent)
                finish() // Opcional: finalize a atividade atual
            }
        })


        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }


    //Dá reload na tela apos o evento do dialog
    private fun initiateQRCodeScan() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(true) // Força a orientação em retrato
        integrator.initiateScan()
    }

    private fun showCustomDialog(layoutResId: Int) {
        // Fechar o diálogo atual se ele estiver ativo
        currentDialog?.dismiss()

        val dialogView = LayoutInflater.from(this).inflate(layoutResId, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        currentDialog = dialog
        dialog.show()

        // Define um temporizador para fechar o diálogo após alguns segundos
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            dialog.dismiss()
            restartActivity()
        }, 2000)

    }

    private fun restartActivity() {
        val intent = Intent(this, EventsActivity::class.java)
        startActivity(intent)
        finish() // Opcional: finalize a atividade atual
    }

    override fun onBackPressed() {
        // Fechar o diálogo quando o botão de voltar for pressionado
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        } else {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val qrCodeData = result.contents
                verifyParticipant(qrCodeData)
            } else {
                showCustomDialog(R.layout.dialog_box_error)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun verifyParticipant(qrCodeData: String) {
        val database = FirebaseDatabase.getInstance().reference
        val eventId = qrCodeData.split("Event ID: ")[1]
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        val sanitizedEmail = userEmail?.replace(".", ",")
        val participantRef = database.child("event_participants").child(eventId).child(sanitizedEmail ?: "")

        participantRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // O participante está cadastrado no evento, então pode ser autenticado
                    participantRef.child("status").setValue("autenticado")
                        .addOnSuccessListener {
                            showCustomDialog(R.layout.dialog_box_confirmed)
                            val participantEmail = snapshot.child("email").getValue(String::class.java)
                            val status = snapshot.child("status").getValue(String::class.java)
                            findViewById<TextView>(R.id.textViewParticipantEmail).text = "Email: $participantEmail"
//
                        }
                        .addOnFailureListener { e ->
                            showCustomDialog(R.layout.dialog_box_error)
                        }
                } else {
                    // O participante não está cadastrado no evento, exibe mensagem de erro
                    showCustomDialog(R.layout.dialog_box_refuse)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                showCustomDialog(R.layout.dialog_box_error)
            }
        })
    }


}

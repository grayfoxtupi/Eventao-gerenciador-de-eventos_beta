package com.example.app3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import android.graphics.Bitmap

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Redirect to login if user is not authenticated
            startActivity(Intent(this, LoginClient::class.java))
            finish()
            return
        }

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextCPF = findViewById<EditText>(R.id.editTextCPF)
        val buttonGenerateQR = findViewById<Button>(R.id.buttonGenerateQR)
        val imageViewQR = findViewById<ImageView>(R.id.imageViewQR)
        val buttonBackToInput = findViewById<Button>(R.id.buttonBackToInput)

        database = FirebaseDatabase.getInstance().reference

        buttonGenerateQR.setOnClickListener {
            val name = editTextName.text.toString()
            val cpf = editTextCPF.text.toString()

            if (name.isNotEmpty() && cpf.length == 11) { // Verifica se o CPF tem exatamente 11 caracteres
                val qrCodeData = "Nome: $name, CPF: $cpf"
                val bitmap = generateQRCode(qrCodeData, 600) // Altera o tamanho do código QR
                imageViewQR.setImageBitmap(bitmap)
                imageViewQR.visibility = View.VISIBLE // Torna o código QR visível

                saveToFirebase(name, cpf)

                // Limpa os campos de texto
                editTextName.text.clear()
                editTextCPF.text.clear()

                // Oculta os campos de texto e o botão
                editTextName.visibility = View.GONE
                editTextCPF.visibility = View.GONE
                buttonGenerateQR.visibility = View.GONE

                // Exibe o botão "Voltar"
                buttonBackToInput.visibility = View.VISIBLE
            } else {
                // Exibe uma mensagem de erro informando que o CPF deve ter exatamente 11 caracteres
                Toast.makeText(this, "O CPF deve ter exatamente 11 caracteres.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura o OnClickListener para o botão "Voltar"
        buttonBackToInput.setOnClickListener {
            // Torna os campos de texto e o botão de geração de QR visíveis
            editTextName.visibility = View.VISIBLE
            editTextCPF.visibility = View.VISIBLE
            buttonGenerateQR.visibility = View.VISIBLE

            // Oculta o código QR e o botão "Voltar"
            imageViewQR.visibility = View.GONE
            buttonBackToInput.visibility = View.GONE
        }
    }

    private fun generateQRCode(data: String, size: Int): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size) // Altera o tamanho do código QR
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

    private fun saveToFirebase(name: String, cpf: String) {
        val user = User(name, cpf)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).setValue(user)
        }
    }
}

data class User(val name: String, val cpf: String)

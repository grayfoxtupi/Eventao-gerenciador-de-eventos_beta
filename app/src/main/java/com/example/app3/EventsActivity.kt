package com.example.app3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class EventsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)


        bottomNavigationView = findViewById(R.id.bottomNavigationAdmin)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.btnHome -> {
                    replaceFragment(FragmentHome())
                    true
                }
                R.id.btnScan -> {
                    // Abre a atividade de leitura de QR code
                    startActivity(Intent(this, QRCodeReaderActivity::class.java))
                    true
                }
                R.id.btnAddEvent -> {
                    replaceFragment(FragmentCreateEvent())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            replaceFragment(FragmentHome())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutNavigationAdmin, fragment)
            .commit()
    }
}

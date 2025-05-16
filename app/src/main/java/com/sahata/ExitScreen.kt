package com.sahata

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ExitScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exit) // Ensure the correct layout is used

        // Find the buttons in the layout
        val kembaliButton = findViewById<ImageView>(R.id.exit_kembali)
        val keluarButton = findViewById<ImageView>(R.id.exit_keluar)

        // Set click listeners for the buttons
        kembaliButton.setOnClickListener {
            // Navigate back to the previous screen
            finish()
        }

        keluarButton.setOnClickListener {
            // Exit the application
            finishAffinity()
        }
    }
}
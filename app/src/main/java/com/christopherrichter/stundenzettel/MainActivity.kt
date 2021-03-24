package com.christopherrichter.stundenzettel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {

    var schicht = Schicht(LocalDateTime.now())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bNeueSchicht: Button = findViewById(R.id.bNeueSchicht)
        bNeueSchicht.setOnClickListener { schichtBeginnen() }
    }

    private fun schichtBeginnen() {
        //TODO Handler für Emails
        val rightNow = LocalDateTime.now()
        Toast.makeText(this, rightNow.toString(), Toast.LENGTH_SHORT).show()
        var schicht = Schicht(rightNow);
        val intent = Intent(this, ZugfahrtenActivity::class.java).apply {
            putExtra("Schicht", schicht)
        }
        startActivity(intent)
    }
}
package com.christopherrichter.stundenzettel

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.christopherrichter.stundenzettel.databinding.ActivityOverviewBinding

class OverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverviewBinding
    private lateinit var schicht: Schicht


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        schicht = intent.getSerializableExtra("Schicht") as Schicht;

    }
}
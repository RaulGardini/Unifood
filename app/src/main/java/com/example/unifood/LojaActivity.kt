package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LojaActivity: AppCompatActivity(){
    private lateinit var tvVoltar: TextView
    private lateinit var tvAdd1: TextView
    private lateinit var tvAdd2: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        tvVoltar = findViewById(R.id.tvVoltar)
        tvAdd1 = findViewById(R.id.tvAdd1)
        tvAdd2 = findViewById(R.id.tvAdd2)

        tvVoltar.setOnClickListener {
            finish()
        }



    }

}
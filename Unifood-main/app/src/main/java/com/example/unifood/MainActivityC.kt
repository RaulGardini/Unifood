package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivityC : AppCompatActivity() {
    lateinit var etEmailRecuperacao: EditText
    lateinit var btnEnviarLink: Button
    lateinit var tvCadastrar: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_c)

        etEmailRecuperacao = findViewById(R.id.etEmailRecuperacao)
        btnEnviarLink = findViewById(R.id.btnEnviarLink)

    }
    override fun onStart() {
        super.onStart()


        tvCadastrar.setOnClickListener {
            Toast.makeText(this, "Tela de cadastro", Toast.LENGTH_SHORT).show()
            val Intent = Intent(this, MainActivityB::class.java)
            startActivity(Intent)
        }

    }
}
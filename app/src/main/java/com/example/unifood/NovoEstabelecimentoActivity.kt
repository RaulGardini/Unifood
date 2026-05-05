package com.example.unifood

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NovoEstabelecimentoActivity : AppCompatActivity() {

    private lateinit var btnVoltar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_estabelecimento)

        btnVoltar = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
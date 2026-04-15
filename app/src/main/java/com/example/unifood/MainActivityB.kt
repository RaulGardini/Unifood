package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // 1. Importação necessária


class MainActivityB : AppCompatActivity() {

    private lateinit var etNomeCompleto: EditText
    private lateinit var etEmailIns: EditText
    private lateinit var etMatricula: EditText
    private lateinit var etSenhaCas: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_b)

        // 4. Você precisa inicializar as variáveis ligando-as ao XML
        etNomeCompleto = findViewById(R.id.etNomeCompleto)
        etEmailIns = findViewById(R.id.etEmailIns)
        etMatricula = findViewById(R.id.etMatricula)
        etSenhaCas = findViewById(R.id.etSenhaCa)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        tvLogin = findViewById(R.id.tvLogin)

        // Exemplo de clique no botão
        btnCadastrar.setOnClickListener {
            // Sua lógica de cadastro aqui
        }
    }
    override fun onStart() {
        super.onStart()
        tvLogin.setOnClickListener {
            Toast.makeText(this, "Redirecionando para a tela de login", Toast.LENGTH_SHORT).show()
            val Intent = Intent(this, MainActivity::class.java)
            startActivity(Intent)
        }

    }
}
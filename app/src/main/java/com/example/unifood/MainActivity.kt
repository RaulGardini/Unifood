package com.example.unifood

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etSenha: EditText
    lateinit var btnEntrar: Button
    lateinit var tvCadastro: TextView
    lateinit var tvErro: TextView
    lateinit var tvEsqueci: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etSenha = findViewById(R.id.etSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        tvCadastro = findViewById(R.id.tvCadastrese)
        tvErro = findViewById(R.id.tvErro)
        tvEsqueci = findViewById(R.id.tvEsqueci)
    }

    override fun onStart() {
        super.onStart()

        btnEntrar.setOnClickListener {
            validarDados()
        }

        tvCadastro.setOnClickListener {
            Toast.makeText(this, "Tela de cadastro em breve", Toast.LENGTH_SHORT).show()
        }

        tvEsqueci.setOnClickListener {
            Toast.makeText(this, "Recuperação de senha em breve", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarDados() {
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString()

        if (email == "aluno@unifor.br" && senha == "123456") {
            tvErro.visibility = android.view.View.GONE
            Toast.makeText(this, "Login válido com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            tvErro.visibility = android.view.View.VISIBLE
        }
    }
}

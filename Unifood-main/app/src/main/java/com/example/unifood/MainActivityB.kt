package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivityB : AppCompatActivity() {

    lateinit var etNomeCompleto: EditText
    lateinit var etEmail: EditText
    lateinit var etMatricula: EditText
    lateinit var etSenha: EditText
    lateinit var btnCadastrar: Button
    lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_b)

        etNomeCompleto = findViewById(R.id.etNomeCompleto)
        etEmail = findViewById(R.id.etEmail)
        etMatricula = findViewById(R.id.etMatricula)
        etSenha = findViewById(R.id.etSenha)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        tvLogin = findViewById(R.id.tvLogin)
    }

    override fun onStart() {
        super.onStart()

        btnCadastrar.setOnClickListener {
            Toast.makeText(this, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                validarCadastro()

        }

        tvLogin.setOnClickListener {
            Toast.makeText(this, "Voltando para login", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun validarCadastro() {
        val nome = etNomeCompleto.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val matricula = etMatricula.text.toString().trim()
        val senha = etSenha.text.toString()


        val nomeCorreto = "Joao Silva"
        val emailCorreto = "aluno@unifor.br"
        val matriculaCorreta = "123456"
        val senhaCorreta = "123456"

        if (nome == nomeCorreto &&
            email == emailCorreto &&
            matricula == matriculaCorreta &&
            senha == senhaCorreta
        ) {
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        } else {
            Toast.makeText(this, "Dados inválidos!", Toast.LENGTH_SHORT).show()
        }
    }
}
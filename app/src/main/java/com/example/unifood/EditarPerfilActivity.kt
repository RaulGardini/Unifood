package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtMatricula: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmail)
        edtMatricula = findViewById(R.id.edtMatricula)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        btnSalvar.setOnClickListener {
            salvarDados()
        }
    }

    private fun salvarDados() {
        val nome = edtNome.text.toString()
        val email = edtEmail.text.toString()
        val matricula = edtMatricula.text.toString()

        if (nome.isEmpty() || email.isEmpty() || matricula.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}
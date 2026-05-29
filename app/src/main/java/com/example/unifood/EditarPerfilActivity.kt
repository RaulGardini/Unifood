package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarPerfilActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

        carregarDados()

        btnBack.setOnClickListener {
            finish()
        }

        btnSalvar.setOnClickListener {
            salvarDados()
        }
    }


    private fun carregarDados() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                edtNome.setText(doc.getString("nome") ?: "")
                edtEmail.setText(doc.getString("email") ?: "")
                edtMatricula.setText(doc.getString("matricula") ?: "")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarDados() {
        val nome = edtNome.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val matricula = edtMatricula.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty() || matricula.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return

        val dados = mapOf(
            "nome" to nome,
            "email" to email,
            "matricula" to matricula
        )

        db.collection("usuarios").document(uid)
            .update(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show()
            }
    }
}
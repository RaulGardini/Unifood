package com.example.unifood

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class NovoEstabelecimentoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_estabelecimento)

        etNome = findViewById(R.id.etNome)
        etEmail = findViewById(R.id.etEmail)
        btnCadastrar = findViewById(R.id.btnCadastrar)

        findViewById<TextView>(R.id.btnVoltar).setOnClickListener { finish() }

        btnCadastrar.setOnClickListener { cadastrar() }
    }

    private fun cadastrar() {
        val nome = etNome.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (nome.isEmpty()) {
            etNome.error = "Digite o nome do estabelecimento"
            etNome.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Digite o email do lojista"
            etEmail.requestFocus()
            return
        }

        btnCadastrar.isEnabled = false

        db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) {
                    Toast.makeText(this, "Nenhum usuário encontrado com esse email", Toast.LENGTH_SHORT).show()
                    btnCadastrar.isEnabled = true
                    return@addOnSuccessListener
                }

                val donoUid = resultado.documents[0].id

                db.collection("estabelecimentos").whereEqualTo("donoUid", donoUid).get()
                    .addOnSuccessListener { estabelecimentos ->
                        if (!estabelecimentos.isEmpty) {
                            Toast.makeText(this, "Este lojista já possui um estabelecimento cadastrado", Toast.LENGTH_LONG).show()
                            btnCadastrar.isEnabled = true
                            return@addOnSuccessListener
                        }

                        val estabelecimento = hashMapOf(
                            "nome" to nome,
                            "donoUid" to donoUid,
                            "localizacao" to "",
                            "categoria" to "",
                            "tempoEntrega" to ""
                        )

                        db.collection("estabelecimentos").add(estabelecimento)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Estabelecimento cadastrado!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao cadastrar estabelecimento", Toast.LENGTH_SHORT).show()
                                btnCadastrar.isEnabled = true
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao verificar estabelecimentos", Toast.LENGTH_SHORT).show()
                        btnCadastrar.isEnabled = true
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar usuário", Toast.LENGTH_SHORT).show()
                btnCadastrar.isEnabled = true
            }
    }
}

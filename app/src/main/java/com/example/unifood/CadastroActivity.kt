package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private lateinit var etNomeCompleto: EditText
    private lateinit var etEmailIns: EditText
    private lateinit var etMatricula: EditText
    private lateinit var etSenhaCas: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var tvLogin: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etNomeCompleto = findViewById(R.id.etNomeCompleto)
        etEmailIns = findViewById(R.id.etEmailIns)
        etMatricula = findViewById(R.id.etMatricula)
        etSenhaCas = findViewById(R.id.etSenhaCa)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        tvLogin = findViewById(R.id.tvLogin)

        btnCadastrar.setOnClickListener {
            cadastrarUsuario()
        }

        tvLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cadastrarUsuario() {
        val nome = etNomeCompleto.text.toString().trim()
        val email = etEmailIns.text.toString().trim()
        val matricula = etMatricula.text.toString().trim()
        val senha = etSenhaCas.text.toString()

        if (nome.isEmpty()) {
            etNomeCompleto.error = "Digite seu nome"
            etNomeCompleto.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmailIns.error = "Digite seu email"
            etEmailIns.requestFocus()
            return
        }

        if (matricula.isEmpty()) {
            etMatricula.error = "Digite sua matrícula"
            etMatricula.requestFocus()
            return
        }

        if (senha.length < 10) {
            etSenhaCas.error = "Senha deve ter ao menos 10 caracteres"
            etSenhaCas.requestFocus()
            return
        }

        if (!senha.matches(Regex(".*[A-Z].*"))) {
            etSenhaCas.error = "Senha deve ter uma letra maiúscula"
            etSenhaCas.requestFocus()
            return
        }

        if (!senha.matches(Regex(".*[0-9!@#\$%^&*()_+\\-=].*"))) {
            etSenhaCas.error = "Senha deve ter um número ou símbolo"
            etSenhaCas.requestFocus()
            return
        }

        btnCadastrar.isEnabled = false

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid
                val usuario = hashMapOf(
                    "nome" to nome,
                    "email" to email,
                    "matricula" to matricula,

                    // Dados da conta
                    "tipo" to "aluno",
                    "status" to "Ativo",

                    // Preferências de notificação
                    "notif_confirmacao" to true,
                    "notif_novidades" to true,
                    "notif_ofertas" to true,
                    "notif_status_pedido" to true
                )

                db.collection("usuarios").document(uid).set(usuario)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        btnCadastrar.isEnabled = true
                        Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                btnCadastrar.isEnabled = true
                Toast.makeText(this, "Erro no cadastro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}

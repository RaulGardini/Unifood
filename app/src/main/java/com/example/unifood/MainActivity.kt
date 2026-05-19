package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etSenha: EditText
    lateinit var btnEntrar: Button
    lateinit var tvCadastro: TextView
    lateinit var tvErro: TextView
    lateinit var tvEsqueci: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val usuarioAtual = auth.currentUser
        if (usuarioAtual != null) {
            redirecionarPorTipo(usuarioAtual.uid)
            return
        }

        mostrarTelaLogin()
    }

    private fun mostrarTelaLogin() {
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etSenha = findViewById(R.id.etSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        tvCadastro = findViewById(R.id.tvCadastrese)
        tvErro = findViewById(R.id.tvErro)
        tvEsqueci = findViewById(R.id.tvEsqueci)

        btnEntrar.setOnClickListener {
            validarDados()
        }

        tvCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        tvEsqueci.setOnClickListener {
            val intent = Intent(this, RecuperarSenhaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validarDados() {
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString()

        if (email.isEmpty()) {
            etEmail.error = "Digite seu email"
            etEmail.requestFocus()
            return
        }

        if (senha.isEmpty()) {
            etSenha.error = "Digite sua senha"
            etSenha.requestFocus()
            return
        }

        btnEntrar.isEnabled = false

        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                tvErro.visibility = View.GONE
                redirecionarPorTipo(result.user!!.uid)
            }
            .addOnFailureListener {
                btnEntrar.isEnabled = true
                tvErro.visibility = View.VISIBLE
            }
    }

    private fun redirecionarPorTipo(uid: String) {
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val tipo = doc.getString("tipo") ?: "aluno"
                val intent = when (tipo) {
                    "admin" -> Intent(this, PainelAdministrativoActivity::class.java)
                    "lojista" -> Intent(this, LojistaActivity::class.java)
                    else -> Intent(this, HomeActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                mostrarTelaLogin()
            }
    }
}

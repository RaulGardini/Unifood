package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class AlterarSenhaActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    private lateinit var tvTelaPedidos: TextView
    private lateinit var tvTelaHome: TextView
    private lateinit var edtSenhaAtual: EditText
    private lateinit var edtNovaSenha: EditText
    private lateinit var edtConfirmarSenha: EditText
    private lateinit var btnBack: TextView
    private lateinit var btnAtualizarSenha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterarsenha)

        btnBack           = findViewById(R.id.btnBack)
        btnAtualizarSenha = findViewById(R.id.btnAtualizarSenha)
        edtSenhaAtual     = findViewById(R.id.edtSenhaAtual)
        edtNovaSenha      = findViewById(R.id.edtNovaSenha)
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha)
        tvTelaHome        = findViewById(R.id.tvTelaHome)
        tvTelaPedidos     = findViewById(R.id.tvTelaPedidos)

        btnBack.setOnClickListener { finish() }

        btnAtualizarSenha.setOnClickListener { tentarAlterarSenha() }

        tvTelaHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        tvTelaPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
            finish()
        }
    }

    private fun tentarAlterarSenha() {
        val senhaAtual      = edtSenhaAtual.text.toString().trim()
        val novaSenha       = edtNovaSenha.text.toString().trim()
        val confirmarSenha  = edtConfirmarSenha.text.toString().trim()

        // Validações locais
        if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (novaSenha != confirmarSenha) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            return
        }
        if (novaSenha.length < 6) {
            Toast.makeText(this, "A nova senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        val email = user?.email

        if (user == null || email == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        btnAtualizarSenha.isEnabled = false

        // Passo 1: Re-autenticar com a senha atual (obrigatório pelo Firebase)
        val credencial = EmailAuthProvider.getCredential(email, senhaAtual)

        user.reauthenticate(credencial)
            .addOnSuccessListener {
                // Passo 2: Atualizar para a nova senha
                user.updatePassword(novaSenha)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Senha alterada com sucesso!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        btnAtualizarSenha.isEnabled = true
                        Toast.makeText(
                            this,
                            "Erro ao atualizar senha: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener {
                btnAtualizarSenha.isEnabled = true
                Toast.makeText(
                    this,
                    "Senha atual incorreta",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var cardEditarPerfil: LinearLayout
    private lateinit var cardAlterarSenha: LinearLayout
    private lateinit var cardNotificacoes: LinearLayout
    private lateinit var tvTelaPedidos: TextView
    private lateinit var tvTelaHome: TextView
    private lateinit var tvLogout: TextView
    private lateinit var btnRoboChat: ImageButton

    private lateinit var tvIniciais: TextView
    private lateinit var tvNomeUsuario: TextView
    private lateinit var tvMatriculaEmail: TextView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        cardEditarPerfil = findViewById(R.id.cardEditarPerfil)
        cardAlterarSenha = findViewById(R.id.cardAlterarSenha)
        cardNotificacoes = findViewById(R.id.cardNotificacoes)
        tvLogout = findViewById(R.id.tvLogout)
        btnRoboChat = findViewById(R.id.btnRobotChat)
        tvTelaPedidos = findViewById(R.id.tvTelaPedidos)
        tvTelaHome = findViewById(R.id.tvTelaHome)

        tvIniciais = findViewById(R.id.tvIniciais)
        tvNomeUsuario = findViewById(R.id.tvNomeUsuario)
        tvMatriculaEmail = findViewById(R.id.tvMatriculaEmail)

        carregarDadosUsuario()
        configurarCliques()
    }
    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
    }
    private fun carregarDadosUsuario() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val nome = doc.getString("nome") ?: "Usuário"
                    val email = doc.getString("email") ?: auth.currentUser?.email ?: "email não informado"
                    val matricula = doc.getString("matricula") ?: "matrícula não informada"

                    tvNomeUsuario.text = nome
                    tvMatriculaEmail.text = "$matricula - $email"
                    tvIniciais.text = gerarIniciais(nome)
                } else {
                    Toast.makeText(this, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gerarIniciais(nome: String): String {
        val partes = nome.trim().split(" ").filter { it.isNotBlank() }

        return when {
            partes.size >= 2 -> "${partes[0].first()}${partes[1].first()}".uppercase()
            partes.size == 1 -> partes[0].take(2).uppercase()
            else -> "US"
        }
    }

    private fun configurarCliques() {
        cardEditarPerfil.setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
        }

        cardAlterarSenha.setOnClickListener {
            startActivity(Intent(this, AlterarSenhaActivity::class.java))
        }

        cardNotificacoes.setOnClickListener {
            startActivity(Intent(this, NotificacoesActivity::class.java))
        }

        btnRoboChat.setOnClickListener {
            startActivity(Intent(this, ChatBotActivity::class.java))
        }

        tvLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        tvTelaPedidos.setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        tvTelaHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}
package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PerfilActivity : AppCompatActivity() {

    private lateinit var cardEditarPerfil: LinearLayout
    private lateinit var cardAlterarSenha: LinearLayout
    private lateinit var cardNotificacoes: LinearLayout
    private lateinit var cardFeedback: LinearLayout
    private lateinit var tvTelaPedidos: TextView
    private lateinit var tvTelaHome: TextView
    private lateinit var tvLogout: TextView
    private lateinit var btnRoboChat: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        cardEditarPerfil = findViewById(R.id.cardEditarPerfil)
        cardAlterarSenha = findViewById(R.id.cardAlterarSenha)
        cardNotificacoes = findViewById(R.id.cardNotificacoes)
        cardFeedback = findViewById(R.id.cardFeedback)
        tvLogout = findViewById(R.id.tvLogout)
        btnRoboChat = findViewById(R.id.btnRobotChat)
        tvTelaPedidos = findViewById(R.id.tvTelaPedidos)
        tvTelaHome = findViewById(R.id.tvTelaHome)

        cardEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(intent)
        }

        cardAlterarSenha.setOnClickListener {
            val intent = Intent(this, AlterarSenhaActivity::class.java)
            startActivity(intent)
        }

        cardNotificacoes.setOnClickListener {
            val intent = Intent(this, NotificacoesActivity::class.java)
            startActivity(intent)
        }

        cardFeedback.setOnClickListener {
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }
        btnRoboChat.setOnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }
        tvLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        tvTelaPedidos.setOnClickListener {
            val intent = Intent(this, CarrinhoActivity::class.java)
            startActivity(intent)
        }
        tvTelaHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }





    }


}
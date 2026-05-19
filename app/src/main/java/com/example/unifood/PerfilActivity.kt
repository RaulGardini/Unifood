package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class PerfilActivity : AppCompatActivity() {

    private lateinit var cardEditarPerfil: LinearLayout
    private lateinit var cardAlterarSenha: LinearLayout
    private lateinit var cardNotificacoes: LinearLayout
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

        btnRoboChat.setOnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }
        tvLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
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
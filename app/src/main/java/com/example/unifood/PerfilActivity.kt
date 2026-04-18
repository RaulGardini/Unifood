package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PerfilActivity : AppCompatActivity() {

    private lateinit var tvEditarPerfil: TextView
    private lateinit var tvAlterarSenha: TextView
    private lateinit var tvNotificacoes: TextView
    private lateinit var tvTelaPedidos: TextView
    private lateinit var tvTelaHome: TextView
    private lateinit var tvLogout: TextView
    private lateinit var btnRoboChat: ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        tvEditarPerfil = findViewById(R.id.tvEditarPerfil)
        tvAlterarSenha = findViewById(R.id.tvAlterarSenha)
        tvNotificacoes = findViewById(R.id.tvNotificacoes)
        tvLogout = findViewById(R.id.tvLogout)
        btnRoboChat = findViewById(R.id.btnRobotChat)
        // Botões da barra de navegação.
        tvTelaPedidos = findViewById(R.id.tvTelaPedidos)
        tvTelaHome = findViewById(R.id.tvTelaHome)



        tvEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(intent)
        }

        tvAlterarSenha.setOnClickListener {

        }
        tvNotificacoes.setOnClickListener {

        }
        btnRoboChat.setOnClickListener {

        }
        tvLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        tvTelaPedidos.setOnClickListener {

        }
        tvTelaHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }





    }


}
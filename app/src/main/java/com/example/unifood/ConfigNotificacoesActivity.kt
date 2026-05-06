package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfigNotificacoesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)

        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        btnVoltar.setOnClickListener {
            finish()
        }

        navInicio.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        navPedidos.setOnClickListener {
            val intent = Intent(this, CarrinhoActivity::class.java)
            startActivity(intent)
            finish()
        }

        navPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

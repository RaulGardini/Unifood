package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChatBotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val btnEnviar = findViewById<ImageButton>(R.id.btnEnviar)
        val edtMensagem = findViewById<EditText>(R.id.edtMensagem)

        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        btnVoltar.setOnClickListener {
            finish()
        }

        btnEnviar.setOnClickListener {
            val msg = edtMensagem.text.toString().trim()
            if (msg.isNotEmpty()) {
                Toast.makeText(this, "Mensagem enviada", Toast.LENGTH_SHORT).show()
                edtMensagem.text.clear()
            }
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

package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LojistaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lojista)

        val btnLogout = findViewById<TextView>(R.id.btnLogout)
        val btnEditarPerfil = findViewById<TextView>(R.id.btnEditarPerfil)
        val btnRecusar = findViewById<Button>(R.id.btnRecusar)
        val btnAceitar = findViewById<Button>(R.id.btnAceitar)
        val navCardapio = findViewById<LinearLayout>(R.id.navCardapio)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilLogistaActivity::class.java)
            startActivity(intent)
        }

        btnRecusar.setOnClickListener {
            Toast.makeText(this, "Pedido recusado", Toast.LENGTH_SHORT).show()
        }

        btnAceitar.setOnClickListener {
            Toast.makeText(this, "Pedido aceito!", Toast.LENGTH_SHORT).show()
        }

        navCardapio.setOnClickListener {
            val intent = Intent(this, CardapioActivity::class.java)
            startActivity(intent)
        }

        navDashboard.setOnClickListener {
        }
    }
}

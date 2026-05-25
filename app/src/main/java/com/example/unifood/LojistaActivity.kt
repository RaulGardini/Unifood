package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LojistaActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lojista)

        val btnLogout = findViewById<TextView>(R.id.btnLogout)
        val btnEditarPerfil = findViewById<TextView>(R.id.btnEditarPerfil)
        val btnRecusar = findViewById<Button>(R.id.btnRecusar)
        val btnAceitar = findViewById<Button>(R.id.btnAceitar)
        val navCardapio = findViewById<LinearLayout>(R.id.navCardapio)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)

        carregarDadosEstabelecimento()

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnEditarPerfil.setOnClickListener {
            startActivity(Intent(this, EditarPerfilLogistaActivity::class.java))
        }

        btnRecusar.setOnClickListener {
            Toast.makeText(this, "Pedido recusado", Toast.LENGTH_SHORT).show()
        }

        btnAceitar.setOnClickListener {
            Toast.makeText(this, "Pedido aceito!", Toast.LENGTH_SHORT).show()
        }

        navCardapio.setOnClickListener {
            startActivity(Intent(this, CardapioActivity::class.java))
        }

        navDashboard.setOnClickListener {}
    }

    // Busca o estabelecimento que pertence a este lojista (pelo campo donoUid)
    private fun carregarDadosEstabelecimento() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("estabelecimentos").whereEqualTo("donoUid", uid).get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) return@addOnSuccessListener
                val doc = resultado.documents[0]
                val nome = doc.getString("nome") ?: ""

                findViewById<TextView>(R.id.tvNomeLoja).text = nome
                findViewById<TextView>(R.id.tvLocalizacao).text = doc.getString("localizacao") ?: ""
                findViewById<TextView>(R.id.tvIniciais).text = nome.take(2).uppercase()
            }
    }
}

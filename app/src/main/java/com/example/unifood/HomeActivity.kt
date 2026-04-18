package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cardLoja1 = findViewById<LinearLayout>(R.id.cardLoja1)
        val cardLoja2 = findViewById<LinearLayout>(R.id.cardLoja2)
        val cardLoja3 = findViewById<LinearLayout>(R.id.cardLoja3)
        val cardLoja4 = findViewById<LinearLayout>(R.id.cardLoja4)

        val onClickLoja = View.OnClickListener {
            val intent = Intent(this, LojaActivity::class.java)
            startActivity(intent)
        }

        cardLoja1.setOnClickListener(onClickLoja)
        cardLoja2.setOnClickListener(onClickLoja)
        cardLoja3.setOnClickListener(onClickLoja)
        cardLoja4.setOnClickListener(onClickLoja)

        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        val indicatorInicio = findViewById<View>(R.id.indicatorInicio)
        val indicatorPedidos = findViewById<View>(R.id.indicatorPedidos)
        val indicatorPerfil = findViewById<View>(R.id.indicatorPerfil)

        val tvNavPedidos = findViewById<TextView>(R.id.tvNavPedidos)
        val tvNavPerfil = findViewById<TextView>(R.id.tvNavPerfil)

        navInicio.setOnClickListener {
            indicatorInicio.setBackgroundResource(R.color.orange)
            indicatorPedidos.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPerfil.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        navPedidos.setOnClickListener {
            indicatorInicio.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPedidos.setBackgroundResource(R.color.orange)
            indicatorPerfil.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            Toast.makeText(this, "Pedidos - Em breve", Toast.LENGTH_SHORT).show()
        }

        navPerfil.setOnClickListener {
            indicatorInicio.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPedidos.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPerfil.setBackgroundResource(R.color.orange)
            Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}

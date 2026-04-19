package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class PedidosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        val indicatorInicio = findViewById<View>(R.id.indicatorInicio)
        val indicatorPedidos = findViewById<View>(R.id.indicatorPedidos)
        val indicatorPerfil = findViewById<View>(R.id.indicatorPerfil)

        navInicio.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        navPedidos.setOnClickListener {
            indicatorInicio.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPedidos.setBackgroundResource(R.color.orange)
            indicatorPerfil.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        navPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

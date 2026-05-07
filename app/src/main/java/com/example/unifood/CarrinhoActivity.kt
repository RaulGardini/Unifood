package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CarrinhoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho)

        val btnPedidos = findViewById<ImageButton>(R.id.btnPedidos)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)
        val btnCartao = findViewById<TextView>(R.id.btnCartao)
        val btnPix = findViewById<TextView>(R.id.btnPix)
        val btnDinheiro = findViewById<TextView>(R.id.btnDinheiro)

        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        btnPedidos.setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }

        btnFinalizar.setOnClickListener {
            Toast.makeText(this, "Pedido finalizado!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }

        btnCartao.setOnClickListener {
            btnCartao.setBackgroundResource(R.drawable.bg_pagamento_selected)
            btnPix.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnDinheiro.setBackgroundResource(R.drawable.bg_pagamento_unselected)
        }

        btnPix.setOnClickListener {
            btnCartao.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnPix.setBackgroundResource(R.drawable.bg_pagamento_selected)
            btnDinheiro.setBackgroundResource(R.drawable.bg_pagamento_unselected)
        }

        btnDinheiro.setOnClickListener {
            btnCartao.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnPix.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnDinheiro.setBackgroundResource(R.drawable.bg_pagamento_selected)
        }

        navInicio.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        navPedidos.setOnClickListener {
            // já está na tela de carrinho/pedidos
        }

        navPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

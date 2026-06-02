
package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CarrinhoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var containerItens: LinearLayout
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnFinalizar: Button
    private lateinit var btnCartao: TextView
    private lateinit var btnPix: TextView
    private lateinit var btnDinheiro: TextView

    private var pagamentoSelecionado = "Pix"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho)

        containerItens = findViewById(R.id.containerItensCarrinho)
        tvSubtotal     = findViewById(R.id.tvSubtotal)
        tvTotal        = findViewById(R.id.tvTotal)
        btnFinalizar   = findViewById(R.id.btnFinalizar)
        btnCartao      = findViewById(R.id.btnCartao)
        btnPix         = findViewById(R.id.btnPix)
        btnDinheiro    = findViewById(R.id.btnDinheiro)

        val btnPedidos = findViewById<ImageButton>(R.id.btnPedidos)
        val navInicio  = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil  = findViewById<LinearLayout>(R.id.navPerfil)

        btnPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }

        btnCartao.setOnClickListener {
            pagamentoSelecionado = "Cartão"
            btnCartao.setBackgroundResource(R.drawable.bg_pagamento_selected)
            btnPix.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnDinheiro.setBackgroundResource(R.drawable.bg_pagamento_unselected)
        }

        btnPix.setOnClickListener {
            pagamentoSelecionado = "Pix"
            btnCartao.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnPix.setBackgroundResource(R.drawable.bg_pagamento_selected)
            btnDinheiro.setBackgroundResource(R.drawable.bg_pagamento_unselected)
        }

        btnDinheiro.setOnClickListener {
            pagamentoSelecionado = "Dinheiro"
            btnCartao.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnPix.setBackgroundResource(R.drawable.bg_pagamento_unselected)
            btnDinheiro.setBackgroundResource(R.drawable.bg_pagamento_selected)
        }

        navInicio.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navPedidos.setOnClickListener {}

        navPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }

        btnFinalizar.setOnClickListener { finalizarPedido() }

        mostrarItens()
    }

    override fun onResume() {
        super.onResume()
        mostrarItens()
    }

    private fun mostrarItens() {
        containerItens.removeAllViews()
        val itens = CarrinhoManager.getItens()

        if (itens.isEmpty()) {
            val vazio = TextView(this)
            vazio.text = "Carrinho vazio"
            vazio.setPadding(32, 32, 32, 32)
            containerItens.addView(vazio)
            btnFinalizar.isEnabled = false
            atualizarTotal()
            return
        }

        btnFinalizar.isEnabled = true

        itens.forEach { item ->
            val card = layoutInflater.inflate(R.layout.item_carrinho_dinamico, containerItens, false)

            card.findViewById<TextView>(R.id.tvNomeItemCarrinho).text = item.nome
            card.findViewById<TextView>(R.id.tvPrecoItemCarrinho).text =
                "R$ %.2f".format(item.preco * item.quantidade)
            card.findViewById<TextView>(R.id.tvQuantidade).text = item.quantidade.toString()

            card.findViewById<TextView>(R.id.btnMais).setOnClickListener {
                CarrinhoManager.adicionar(item)
                mostrarItens()
            }

            card.findViewById<TextView>(R.id.btnMenos).setOnClickListener {
                CarrinhoManager.remover(item.id)
                mostrarItens()
            }

            containerItens.addView(card)
        }

        atualizarTotal()
    }

    private fun atualizarTotal() {
        val total = CarrinhoManager.getTotal()
        tvSubtotal.text = "R$ %.2f".format(total)
        tvTotal.text = "R$ %.2f".format(total)
        btnFinalizar.text = "Finalizar pedido - R$ %.2f".format(total)
    }

    private fun finalizarPedido() {
        val uid = auth.currentUser?.uid ?: return
        val itens = CarrinhoManager.getItens()

        if (itens.isEmpty()) {
            Toast.makeText(this, "Carrinho vazio", Toast.LENGTH_SHORT).show()
            return
        }

        btnFinalizar.isEnabled = false

        val itensMap = itens.map { item ->
            hashMapOf(
                "nome"       to item.nome,
                "preco"      to item.preco,
                "quantidade" to item.quantidade
            )
        }

        val codigo = "UF-${(1000..9999).random()}"

        val pedido = hashMapOf(
            "usuarioUid"  to uid,
            "lojaId"      to CarrinhoManager.lojaId,
            "lojaNome"    to CarrinhoManager.lojaNome,
            "itens"       to itensMap,
            "total"       to CarrinhoManager.getTotal(),
            "pagamento"   to pagamentoSelecionado,
            "status"      to "recebido",
            "codigo"      to codigo,
            "data"        to System.currentTimeMillis()
        )

        db.collection("pedidos").add(pedido)
            .addOnSuccessListener { docRef ->
                Toast.makeText(this, "Pedido $codigo finalizado!", Toast.LENGTH_SHORT).show()

                val lojaIdFinal = CarrinhoManager.lojaId
                CarrinhoManager.limpar()

                val intent = Intent(this, PedidosActivity::class.java)
                intent.putExtra("lojaId", lojaIdFinal)
                intent.putExtra("pedidoId", docRef.id)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                btnFinalizar.isEnabled = true
                Toast.makeText(this, "Erro ao finalizar pedido", Toast.LENGTH_SHORT).show()
            }
    }
}
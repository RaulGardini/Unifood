package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LojaActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var containerItens: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        containerItens = findViewById(R.id.containerItens)
        val tvVoltar = findViewById<TextView>(R.id.tvVoltar)
        val tvNomeLoja = findViewById<TextView>(R.id.tvNomeLoja)
        val tvLocalizacao = findViewById<TextView>(R.id.tvLocalizacao)
        val tvTempo = findViewById<TextView>(R.id.tvTempo)

        tvVoltar.setOnClickListener { finish() }

        val lojaId = intent.getStringExtra("lojaId") ?: return
        val lojaNome = intent.getStringExtra("lojaNome") ?: ""

        tvNomeLoja.text = lojaNome

        // Carrega dados da loja
        db.collection("estabelecimentos").document(lojaId).get()
            .addOnSuccessListener { doc ->
                tvLocalizacao.text = doc.getString("localizacao") ?: ""
                tvTempo.text = doc.getString("tempoEntrega") ?: ""
            }

        // Carrega itens do cardápio
        carregarCardapio(lojaId)
    }

    private fun carregarCardapio(lojaId: String) {
        db.collection("estabelecimentos").document(lojaId)
            .collection("cardapio").get()
            .addOnSuccessListener { resultado ->
                containerItens.removeAllViews()

                if (resultado.isEmpty) {
                    val vazio = TextView(this)
                    vazio.text = "Nenhum item no cardápio"
                    vazio.setPadding(32, 32, 32, 32)
                    containerItens.addView(vazio)
                    return@addOnSuccessListener
                }

                for (doc in resultado.documents) {
                    val item = ItemCardapio(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        descricao = doc.getString("descricao") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoria = doc.getString("categoria") ?: "",
                        disponivel = doc.getBoolean("disponivel") ?: true
                    )

                    if (item.disponivel) {
                        containerItens.addView(criarCardItem(item))
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar cardápio", Toast.LENGTH_SHORT).show()
            }
    }

    private fun criarCardItem(item: ItemCardapio): View {
        val card = layoutInflater.inflate(R.layout.item_cardapio_cliente, containerItens, false)
        card.findViewById<TextView>(R.id.tvNomeItem).text = item.nome
        card.findViewById<TextView>(R.id.tvDescricaoItem).text = item.descricao
        card.findViewById<TextView>(R.id.tvPrecoItem).text = "R$ %.2f".format(item.preco)

        card.findViewById<TextView>(R.id.btnAddItem).setOnClickListener {
            Toast.makeText(this, "${item.nome} adicionado!", Toast.LENGTH_SHORT).show()
        }

        return card
    }
}

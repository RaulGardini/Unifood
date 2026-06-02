package com.example.unifood

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LojaActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private var lojaId: String = ""
    private var categoriaAtual: String? = null
    private var todosItens = listOf<ItemCardapio>()

    private lateinit var containerItens: LinearLayout
    private lateinit var containerCategorias: LinearLayout
    private lateinit var tvNomeLoja: TextView
    private lateinit var tvLocalizacao: TextView
    private lateinit var tvCategoria: TextView
    private lateinit var tvTempo: TextView
    private lateinit var tvTempoEntregaInfo: TextView
    private lateinit var tvMediaAvaliacao: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        containerItens = findViewById(R.id.containerItens)
        containerCategorias = findViewById(R.id.containerCategorias)
        tvNomeLoja = findViewById(R.id.tvNomeLoja)
        tvLocalizacao = findViewById(R.id.tvLocalizacao)
        tvCategoria = findViewById(R.id.tvCategoria)
        tvTempo = findViewById(R.id.tvTempo)
        tvTempoEntregaInfo = findViewById(R.id.tvTempoEntregaInfo)
        tvMediaAvaliacao = findViewById(R.id.tvMediaAvaliacao)

        findViewById<TextView>(R.id.tvVoltar).setOnClickListener { finish() }

        lojaId = intent.getStringExtra("lojaId") ?: return
        val lojaNome = intent.getStringExtra("lojaNome") ?: ""
        tvNomeLoja.text = lojaNome

        carregarDadosLoja(lojaId)
        carregarCardapio(lojaId)
        carregarMediaAvaliacoes(lojaId)
    }

    private fun carregarDadosLoja(lojaId: String) {
        db.collection("estabelecimentos").document(lojaId).get()
            .addOnSuccessListener { doc ->
                val nome = doc.getString("nome") ?: ""
                val localizacao = doc.getString("localizacao") ?: ""
                val tempoEntrega = doc.getString("tempoEntrega") ?: ""
                val horarioFim = doc.getString("horarioFim") ?: ""

                tvNomeLoja.text = nome
                tvLocalizacao.text = localizacao

                tvTempo.text = if (horarioFim.isNotEmpty()) {
                    "Aberto até $horarioFim"
                } else {
                    tempoEntrega
                }

                tvTempoEntregaInfo.text = if (tempoEntrega.isNotEmpty()) {
                    "⏱ $tempoEntrega"
                } else {
                    ""
                }

                val categorias = doc.get("categorias") as? List<*> ?: emptyList<String>()
                tvCategoria.text = if (categorias.isNotEmpty()) {
                    categorias.first().toString()
                } else {
                    ""
                }

                montarAbaCategorias(categorias.map { it.toString() })
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar loja", Toast.LENGTH_SHORT).show()
            }
    }

    private fun montarAbaCategorias(categorias: List<String>) {
        containerCategorias.removeAllViews()

        if (categorias.isEmpty()) return

        if (categoriaAtual == null) categoriaAtual = categorias.first()

        for (cat in categorias) {
            val tv = TextView(this).apply {
                text = cat
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
                setPadding(44, 0, 44, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 64
                ).apply { marginEnd = 16 }
            }

            atualizarEstiloAba(tv, cat)

            tv.setOnClickListener {
                categoriaAtual = cat
                montarAbaCategorias(categorias)
                mostrarItensFiltrados()
            }

            containerCategorias.addView(tv)
        }

        mostrarItensFiltrados()
    }

    private fun atualizarEstiloAba(tv: TextView, categoria: String) {
        if (categoriaAtual == categoria) {
            tv.setBackgroundResource(R.drawable.bg_category_selected)
            tv.setTextColor(Color.WHITE)
        } else {
            tv.setBackgroundColor(Color.TRANSPARENT)
            tv.setTextColor(Color.parseColor("#999999"))
        }
    }

    private fun carregarMediaAvaliacoes(lojaId: String) {
        db.collection("avaliacoes")
            .whereEqualTo("lojaId", lojaId)
            .get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) {
                    tvMediaAvaliacao.text = "⭐ 0.0"
                    return@addOnSuccessListener
                }

                var soma = 0.0
                var quantidade = 0

                for (doc in resultado.documents) {
                    val nota = doc.getDouble("nota")
                    if (nota != null) {
                        soma += nota
                        quantidade++
                    }
                }

                val media = if (quantidade > 0) soma / quantidade else 0.0
                tvMediaAvaliacao.text = "⭐ %.1f".format(media)
            }
            .addOnFailureListener {
                tvMediaAvaliacao.text = "⭐ 0.0"
            }
    }

    private fun carregarCardapio(lojaId: String) {
        db.collection("estabelecimentos")
            .document(lojaId)
            .collection("cardapio")
            .get()
            .addOnSuccessListener { resultado ->
                todosItens = resultado.documents.map { doc ->
                    ItemCardapio(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        descricao = doc.getString("descricao") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoria = doc.getString("categoria") ?: "",
                        disponivel = doc.getBoolean("disponivel") ?: true
                    )
                }
                mostrarItensFiltrados()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar cardápio", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarItensFiltrados() {
        containerItens.removeAllViews()

        val itensFiltrados = todosItens.filter {
            it.disponivel && it.categoria.equals(categoriaAtual, ignoreCase = true)
        }

        if (itensFiltrados.isEmpty()) {
            val vazio = TextView(this)
            vazio.text = "Nenhum item encontrado"
            vazio.setPadding(32, 32, 32, 32)
            containerItens.addView(vazio)
            return
        }

        itensFiltrados.forEach { item ->
            containerItens.addView(criarCardItem(item))
        }
    }

    private fun criarCardItem(item: ItemCardapio): View {
        val card = layoutInflater.inflate(R.layout.item_cardapio_cliente, containerItens, false)

        card.findViewById<TextView>(R.id.tvNomeItem).text = item.nome
        card.findViewById<TextView>(R.id.tvDescricaoItem).text = item.descricao
        card.findViewById<TextView>(R.id.tvPrecoItem).text = "R$ %.2f".format(item.preco)

        card.findViewById<TextView>(R.id.btnAddItem).setOnClickListener {
            CarrinhoManager.lojaId = lojaId
            CarrinhoManager.lojaNome = tvNomeLoja.text.toString()
            CarrinhoManager.adicionar(
                CarrinhoManager.ItemCarrinho(
                    id = item.id,
                    nome = item.nome,
                    preco = item.preco
                )
            )
            Toast.makeText(this, "${item.nome} adicionado!", Toast.LENGTH_SHORT).show()
        }

        return card
    }
}

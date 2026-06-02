package com.example.unifood

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RelatoriosActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var tvTotalVendas: TextView
    private lateinit var tvTotalPedidos: TextView
    private lateinit var containerVendasEstab: LinearLayout
    private lateinit var containerProdutos: LinearLayout
    private lateinit var spinnerMes: Spinner

    private var nomeEstabelecimentos = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorios)

        tvTotalVendas = findViewById(R.id.tvTotalVendas)
        tvTotalPedidos = findViewById(R.id.tvTotalPedidos)
        containerVendasEstab = findViewById(R.id.containerVendasEstab)
        containerProdutos = findViewById(R.id.containerProdutos)
        spinnerMes = findViewById(R.id.spinnerMes)

        findViewById<TextView>(R.id.btnVoltar).setOnClickListener { finish() }

        val meses = listOf(
            "Todos os meses",
            "Janeiro", "Fevereiro", "Março", "Abril",
            "Maio", "Junho", "Julho", "Agosto",
            "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMes.adapter = adapter

        spinnerMes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                carregarDados(if (position == 0) null else position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        carregarNomesEstabelecimentos()
    }

    private fun carregarNomesEstabelecimentos() {
        db.collection("estabelecimentos").get()
            .addOnSuccessListener { resultado ->
                for (doc in resultado.documents) {
                    nomeEstabelecimentos[doc.id] = doc.getString("nome") ?: "Sem nome"
                }
                carregarDados(null)
            }
    }

    private fun carregarDados(mes: Int?) {
        db.collection("pedidos").get()
            .addOnSuccessListener { resultado ->
                val pedidosFiltrados = resultado.documents.filter { doc ->
                    if (mes == null) return@filter true
                    val data = doc.getLong("data") ?: return@filter false
                    val cal = Calendar.getInstance().apply { timeInMillis = data }
                    cal.get(Calendar.MONTH) + 1 == mes
                }

                var totalVendas = 0.0
                val vendasPorLoja = mutableMapOf<String, Double>()
                val quantidadePorProduto = mutableMapOf<String, Int>()

                for (doc in pedidosFiltrados) {
                    val total = doc.getDouble("total") ?: 0.0
                    val lojaId = doc.getString("lojaId") ?: "desconhecido"
                    totalVendas += total
                    vendasPorLoja[lojaId] = (vendasPorLoja[lojaId] ?: 0.0) + total

                    val itens = doc.get("itens") as? List<Map<String, Any>> ?: continue
                    for (item in itens) {
                        val nome = item["nome"] as? String ?: continue
                        val qtd = (item["quantidade"] as? Long)?.toInt() ?: 1
                        quantidadePorProduto[nome] = (quantidadePorProduto[nome] ?: 0) + qtd
                    }
                }

                tvTotalPedidos.text = pedidosFiltrados.size.toString()
                tvTotalVendas.text = formatarValor(totalVendas)

                mostrarVendasPorEstabelecimento(vendasPorLoja)
                mostrarProdutosMaisVendidos(quantidadePorProduto)
            }
    }

    private fun mostrarVendasPorEstabelecimento(vendasPorLoja: Map<String, Double>) {
        containerVendasEstab.removeAllViews()
        if (vendasPorLoja.isEmpty()) return

        val ordenado = vendasPorLoja.entries.sortedByDescending { it.value }
        val maximo = ordenado.first().value
        val cores = arrayOf(Color.parseColor("#FF6B00"), Color.parseColor("#123B63"))

        ordenado.forEachIndexed { index, (lojaId, valor) ->
            val nome = nomeEstabelecimentos[lojaId] ?: lojaId
            val peso = if (maximo > 0) (valor / maximo).toFloat() else 0f
            val cor = cores[index % cores.size]
            containerVendasEstab.addView(criarBarra(nome, formatarValor(valor), peso, cor))
        }
    }

    private fun mostrarProdutosMaisVendidos(quantidadePorProduto: Map<String, Int>) {
        containerProdutos.removeAllViews()
        if (quantidadePorProduto.isEmpty()) return

        val ordenado = quantidadePorProduto.entries.sortedByDescending { it.value }.take(5)
        val maximo = ordenado.first().value
        val cores = arrayOf(Color.parseColor("#FF6B00"), Color.parseColor("#123B63"))

        ordenado.forEachIndexed { index, (nome, qtd) ->
            val peso = if (maximo > 0) qtd.toFloat() / maximo.toFloat() else 0f
            val cor = cores[index % cores.size]
            containerProdutos.addView(criarBarra(nome, "$qtd un", peso, cor))
        }
    }

    private fun criarBarra(nome: String, valorTexto: String, peso: Float, cor: Int): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 10 }
        }

        val tvNome = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(100.dp(), LinearLayout.LayoutParams.WRAP_CONTENT)
            text = nome
            setTextColor(Color.parseColor("#444444"))
            textSize = 13f
            gravity = Gravity.END
            setPadding(0, 0, 8.dp(), 0)
        }

        val barra = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 14.dp(), peso.coerceAtLeast(0.05f))
            setBackgroundColor(cor)
        }

        val tvValor = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = 8.dp() }
            text = valorTexto
            setTextColor(Color.parseColor("#123B63"))
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        row.addView(tvNome)
        row.addView(barra)
        row.addView(tvValor)
        return row
    }

    private fun formatarValor(valor: Double): String {
        return if (valor >= 1000) {
            "R$ %.1fk".format(valor / 1000)
        } else {
            "R$ %.2f".format(valor)
        }
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}

package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var containerLojas: LinearLayout
    private lateinit var tvVazio: TextView
    private lateinit var etBusca: EditText

    private var todasLojas = listOf<Loja>()
    private var filtroAtual = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        containerLojas = findViewById(R.id.containerLojas)
        tvVazio = findViewById(R.id.tvVazio)
        etBusca = findViewById(R.id.etBusca)

        carregarNomeUsuario()
        carregarLojas()
        configurarFiltros()
        configurarBusca()
        configurarNavegacao()
    }

    // Busca o nome do usuário logado e mostra na saudação
    private fun carregarNomeUsuario() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val nome = doc.getString("nome") ?: "Usuário"
                findViewById<TextView>(R.id.tvSaudacao).text = "Olá, $nome"
            }
    }

    // Busca todos os estabelecimentos do Firestore
    private fun carregarLojas() {
        db.collection("estabelecimentos").get()
            .addOnSuccessListener { resultado ->
                todasLojas = resultado.documents.map { doc ->
                    Loja(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        categoria = doc.getString("categoria") ?: "",
                        tempoEntrega = doc.getString("tempoEntrega") ?: "",
                        localizacao = doc.getString("localizacao") ?: ""
                    )
                }
                filtrar()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar estabelecimentos", Toast.LENGTH_SHORT).show()
            }
    }

    // Aplica o filtro de categoria + busca por texto e atualiza a tela
    private fun filtrar() {
        var lista = todasLojas

        if (filtroAtual != "Todos") {
            lista = lista.filter { it.categoria.equals(filtroAtual, ignoreCase = true) }
        }

        val busca = etBusca.text.toString()
        if (busca.isNotBlank()) {
            lista = lista.filter { it.nome.contains(busca, ignoreCase = true) }
        }

        mostrarLojas(lista)
    }

    // Cria os cards das lojas na tela, 2 por linha (igual ao layout original)
    private fun mostrarLojas(lojas: List<Loja>) {
        containerLojas.removeAllViews()
        tvVazio.visibility = if (lojas.isEmpty()) View.VISIBLE else View.GONE

        var i = 0
        while (i < lojas.size) {
            val linha = LinearLayout(this)
            linha.orientation = LinearLayout.HORIZONTAL
            linha.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dpToPx(12) }

            linha.addView(criarCard(lojas[i]))

            if (i + 1 < lojas.size) {
                linha.addView(criarCard(lojas[i + 1]))
            } else {
                linha.addView(View(this), LinearLayout.LayoutParams(0, 0, 1f))
            }

            containerLojas.addView(linha)
            i += 2
        }
    }

    // Infla um card de loja e preenche com os dados
    private fun criarCard(loja: Loja): View {
        val card = layoutInflater.inflate(R.layout.item_loja, null)
        card.findViewById<TextView>(R.id.tvNomeLoja).text = loja.nome
        card.findViewById<TextView>(R.id.tvTempoEntrega).text = loja.tempoEntrega
        card.findViewById<TextView>(R.id.tvLocalizacao).text = loja.localizacao

        card.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dpToPx(6)
            marginEnd = dpToPx(6)
        }

        card.setOnClickListener {
            val intent = Intent(this, LojaActivity::class.java)
            intent.putExtra("lojaId", loja.id)
            intent.putExtra("lojaNome", loja.nome)
            startActivity(intent)
        }

        return card
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    // Configura os botões de filtro (Todos, Lanches, Saudável)
    private fun configurarFiltros() {
        val filtroTodos = findViewById<TextView>(R.id.tvFiltroTodos)
        val filtroLanches = findViewById<TextView>(R.id.tvFiltroLanches)
        val filtroSaudavel = findViewById<TextView>(R.id.tvFiltroSaudavel)
        val filtros = listOf(filtroTodos, filtroLanches, filtroSaudavel)

        fun selecionarFiltro(selecionado: TextView, categoria: String) {
            filtros.forEach { tv ->
                if (tv == selecionado) {
                    tv.setBackgroundResource(R.drawable.bg_category_selected)
                    tv.setTextColor(0xFFFFFFFF.toInt())
                } else {
                    tv.setBackgroundResource(R.drawable.bg_category_unselected)
                    tv.setTextColor(0xFF555555.toInt())
                }
            }
            filtroAtual = categoria
            filtrar()
        }

        filtroTodos.setOnClickListener { selecionarFiltro(filtroTodos, "Todos") }
        filtroLanches.setOnClickListener { selecionarFiltro(filtroLanches, "Lanches") }
        filtroSaudavel.setOnClickListener { selecionarFiltro(filtroSaudavel, "Saudável") }
    }

    // Busca em tempo real pelo nome da loja
    private fun configurarBusca() {
        etBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { filtrar() }
        })
    }

    // Navegação inferior (Inicio, Pedidos, Perfil)
    private fun configurarNavegacao() {
        val indicatorInicio = findViewById<View>(R.id.indicatorInicio)
        val indicatorPedidos = findViewById<View>(R.id.indicatorPedidos)
        val indicatorPerfil = findViewById<View>(R.id.indicatorPerfil)

        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            indicatorInicio.setBackgroundResource(R.color.orange)
            indicatorPedidos.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPerfil.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        findViewById<LinearLayout>(R.id.navPedidos).setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            indicatorInicio.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPedidos.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            indicatorPerfil.setBackgroundResource(R.color.orange)
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }
}

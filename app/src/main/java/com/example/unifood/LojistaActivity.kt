package com.example.unifood

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Calendar

class LojistaActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var lojaId: String = ""
    private var abaAtual: String = "recebido"
    private var listenerPedidos: ListenerRegistration? = null
    private var todosPedidos: List<com.google.firebase.firestore.DocumentSnapshot> = emptyList()

    private lateinit var tvPedidosFila: TextView
    private lateinit var tvPedidosHoje: TextView
    private lateinit var tvFaturamento: TextView
    private lateinit var tvBadgeNovos: TextView
    private lateinit var tvBadgePreparando: TextView
    private lateinit var tvBadgeProntos: TextView
    private lateinit var tabNovos: TextView
    private lateinit var tabPreparando: TextView
    private lateinit var tabProntos: TextView
    private lateinit var containerPedidos: LinearLayout
    private lateinit var tvSemPedidos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lojista)

        tvPedidosFila = findViewById(R.id.tvPedidosFila)
        tvPedidosHoje = findViewById(R.id.tvPedidosHoje)
        tvFaturamento = findViewById(R.id.tvFaturamento)
        tvBadgeNovos = findViewById(R.id.tvBadgeNovos)
        tvBadgePreparando = findViewById(R.id.tvBadgePreparando)
        tvBadgeProntos = findViewById(R.id.tvBadgeProntos)
        tabNovos = findViewById(R.id.tabNovos)
        tabPreparando = findViewById(R.id.tabPreparando)
        tabProntos = findViewById(R.id.tabProntos)
        containerPedidos = findViewById(R.id.containerPedidos)
        tvSemPedidos = findViewById(R.id.tvSemPedidos)

        findViewById<TextView>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btnEditarPerfil).setOnClickListener {
            startActivity(Intent(this, EditarPerfilLogistaActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navCardapio).setOnClickListener {
            startActivity(Intent(this, CardapioActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navDashboard).setOnClickListener { }

        configurarTabs()
        carregarDadosEstabelecimento()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerPedidos?.remove()
    }

    private fun configurarTabs() {
        tabNovos.setOnClickListener { selecionarAba("recebido") }
        tabPreparando.setOnClickListener { selecionarAba("preparando") }
        tabProntos.setOnClickListener { selecionarAba("pronto") }
        selecionarAba("recebido")
    }

    private fun selecionarAba(aba: String) {
        abaAtual = aba
        tabNovos.setBackgroundResource(if (aba == "recebido") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive)
        tabNovos.setTextColor(if (aba == "recebido") Color.WHITE else Color.parseColor("#333333"))
        tabPreparando.setBackgroundResource(if (aba == "preparando") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive)
        tabPreparando.setTextColor(if (aba == "preparando") Color.WHITE else Color.parseColor("#333333"))
        tabProntos.setBackgroundResource(if (aba == "pronto") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive)
        tabProntos.setTextColor(if (aba == "pronto") Color.WHITE else Color.parseColor("#333333"))
        filtrarEMostrar()
    }

    private fun filtrarEMostrar() {
        val pedidosAba = todosPedidos.filter { it.getString("status") == abaAtual }
        mostrarPedidos(pedidosAba)
    }

    private fun carregarDadosEstabelecimento() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("estabelecimentos").whereEqualTo("donoUid", uid).get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) return@addOnSuccessListener
                val doc = resultado.documents[0]
                lojaId = doc.id
                val nome = doc.getString("nome") ?: ""

                findViewById<TextView>(R.id.tvNomeLoja).text = nome
                findViewById<TextView>(R.id.tvLocalizacao).text = doc.getString("localizacao") ?: ""
                findViewById<TextView>(R.id.tvIniciais).text = nome.take(2).uppercase()

                ouvirPedidos()
            }
    }

    private fun ouvirPedidos() {
        if (lojaId.isEmpty()) return

        listenerPedidos = db.collection("pedidos")
            .whereEqualTo("lojaId", lojaId)
            .addSnapshotListener { resultado, erro ->
                if (erro != null || resultado == null) return@addSnapshotListener

                todosPedidos = resultado.documents

                val novos = todosPedidos.filter { it.getString("status") == "recebido" }
                val preparando = todosPedidos.filter { it.getString("status") == "preparando" }
                val prontos = todosPedidos.filter { it.getString("status") == "pronto" }

                tvBadgeNovos.text = novos.size.toString()
                tvBadgePreparando.text = preparando.size.toString()
                tvBadgeProntos.text = prontos.size.toString()

                val pedidosFila = novos.size + preparando.size
                tvPedidosFila.text = pedidosFila.toString()

                val inicioHoje = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val pedidosHoje = todosPedidos.filter {
                    (it.getLong("data") ?: 0L) >= inicioHoje
                }
                tvPedidosHoje.text = pedidosHoje.size.toString()

                var faturamento = 0.0
                for (p in pedidosHoje) {
                    faturamento += p.getDouble("total") ?: 0.0
                }
                tvFaturamento.text = "R$ %.2f".format(faturamento)

                filtrarEMostrar()
            }
    }

    private fun mostrarPedidos(pedidos: List<com.google.firebase.firestore.DocumentSnapshot>) {
        containerPedidos.removeAllViews()

        if (pedidos.isEmpty()) {
            tvSemPedidos.visibility = View.VISIBLE
            return
        }

        tvSemPedidos.visibility = View.GONE

        for (doc in pedidos) {
            containerPedidos.addView(criarCardPedido(doc))
        }
    }

    private fun criarCardPedido(doc: com.google.firebase.firestore.DocumentSnapshot): View {
        val pedidoId = doc.id
        val codigo = doc.getString("codigo") ?: pedidoId.take(6)
        val status = doc.getString("status") ?: "recebido"
        val total = doc.getDouble("total") ?: 0.0
        val itens = doc.get("itens") as? List<Map<String, Any>> ?: emptyList()

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_card_stat)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(12) }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(6) }
        }

        val tvCodigo = TextView(this).apply {
            text = "#$codigo"
            setTextColor(Color.parseColor("#333333"))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        header.addView(tvCodigo)
        card.addView(header)

        for (item in itens) {
            val nome = item["nome"] as? String ?: ""
            val qtd = (item["quantidade"] as? Long)?.toInt() ?: 1
            val tvItem = TextView(this).apply {
                text = "${qtd}x $nome"
                setTextColor(Color.parseColor("#888888"))
                textSize = 13f
            }
            card.addView(tvItem)
        }

        val tvTotal = TextView(this).apply {
            text = "R$ %.2f".format(total)
            setTextColor(Color.parseColor("#FF6B00"))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(6)
                bottomMargin = dp(12)
            }
        }
        card.addView(tvTotal)

        val botoes = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        if (status != "pronto") {
            val btnRecusar = Button(this).apply {
                text = "Recusar"
                setTextColor(Color.parseColor("#D32F2F"))
                setBackgroundResource(R.drawable.bg_btn_recusar)
                textSize = 13f
                isAllCaps = false
                layoutParams = LinearLayout.LayoutParams(0, dp(40), 1f).apply {
                    marginEnd = dp(10)
                }
            }
            btnRecusar.setOnClickListener {
                db.collection("pedidos").document(pedidoId)
                    .update("status", "cancelado")
                    .addOnSuccessListener {
                        Toast.makeText(this, "Pedido recusado", Toast.LENGTH_SHORT).show()
                    }
            }
            botoes.addView(btnRecusar)
        }

        val proximoStatus = when (status) {
            "recebido" -> "preparando"
            "preparando" -> "pronto"
            "pronto" -> "entregue"
            else -> null
        }

        val textoBtn = when (status) {
            "recebido" -> "Aceitar"
            "preparando" -> "Pronto"
            "pronto" -> "Entregar"
            else -> ""
        }

        if (proximoStatus != null) {
            val btnAvancar = Button(this).apply {
                text = textoBtn
                setTextColor(Color.WHITE)
                setBackgroundResource(R.drawable.bg_btn_aceitar)
                textSize = 13f
                isAllCaps = false
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, dp(40), 1f)
            }
            btnAvancar.setOnClickListener {
                db.collection("pedidos").document(pedidoId)
                    .update("status", proximoStatus)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Status atualizado!", Toast.LENGTH_SHORT).show()
                    }
            }
            botoes.addView(btnAvancar)
        }

        card.addView(botoes)
        return card
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

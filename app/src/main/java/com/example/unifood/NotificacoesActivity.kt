package com.example.unifood

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificacoesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var containerNotificacoes: LinearLayout
    private lateinit var tvVazio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificacoes)

        containerNotificacoes = findViewById(R.id.containerNotificacoes)
        tvVazio = findViewById(R.id.tvVazio)

        findViewById<TextView>(R.id.btnVoltar).setOnClickListener { finish() }

        findViewById<TextView>(R.id.btnConfig).setOnClickListener {
            startActivity(Intent(this, ConfigNotificacoesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navPedidos).setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }

        carregarNotificacoes()
    }

    private fun carregarNotificacoes() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("pedidos")
            .whereEqualTo("usuarioUid", uid)
            .get()
            .addOnSuccessListener { resultado ->
                containerNotificacoes.removeAllViews()

                val notificacoes = mutableListOf<Triple<String, String, Long>>()

                for (doc in resultado.documents) {
                    val codigo = doc.getString("codigo") ?: ""
                    val status = doc.getString("status") ?: ""
                    val data = doc.getLong("data") ?: 0L

                    when (status) {
                        "recebido" -> notificacoes.add(
                            Triple("Pedido confirmado", "Seu pedido #$codigo foi recebido.", data)
                        )
                        "preparando" -> {
                            notificacoes.add(
                                Triple("Pedido confirmado", "Seu pedido #$codigo foi recebido.", data)
                            )
                            notificacoes.add(
                                Triple("Pedido em preparo", "Seu pedido #$codigo está sendo preparado.", data)
                            )
                        }
                        "pronto" -> {
                            notificacoes.add(
                                Triple("Pedido confirmado", "Seu pedido #$codigo foi recebido.", data)
                            )
                            notificacoes.add(
                                Triple("Pedido em preparo", "Seu pedido #$codigo está sendo preparado.", data)
                            )
                            notificacoes.add(
                                Triple("Pedido pronto!", "Seu pedido #$codigo está pronto para retirada!", data)
                            )
                        }
                        "entregue" -> {
                            notificacoes.add(
                                Triple("Pedido confirmado", "Seu pedido #$codigo foi recebido.", data)
                            )
                            notificacoes.add(
                                Triple("Pedido em preparo", "Seu pedido #$codigo está sendo preparado.", data)
                            )
                            notificacoes.add(
                                Triple("Pedido pronto!", "Seu pedido #$codigo está pronto para retirada!", data)
                            )
                            notificacoes.add(
                                Triple("Pedido entregue", "Seu pedido #$codigo foi entregue!", data)
                            )
                        }
                    }
                }

                notificacoes.sortByDescending { it.third }

                if (notificacoes.isEmpty()) {
                    tvVazio.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                tvVazio.visibility = View.GONE

                for ((titulo, mensagem, data) in notificacoes) {
                    containerNotificacoes.addView(criarCardNotificacao(titulo, mensagem, data))
                }
            }
    }

    private fun criarCardNotificacao(titulo: String, mensagem: String, data: Long): View {
        val frame = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 10 }
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(24, 24, 24, 24)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val tvTitulo = TextView(this).apply {
            text = titulo
            setTextColor(Color.BLACK)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvMensagem = TextView(this).apply {
            text = mensagem
            setTextColor(Color.parseColor("#555555"))
            textSize = 14f
        }

        val tvTempo = TextView(this).apply {
            text = tempoRelativo(data)
            setTextColor(Color.parseColor("#999999"))
            textSize = 12f
        }

        card.addView(tvTitulo)
        card.addView(tvMensagem)
        card.addView(tvTempo)

        val dot = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(20, 20).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 16, 16, 0)
            }
            setBackgroundResource(R.drawable.bg_dot_orange)
        }

        val agora = System.currentTimeMillis()
        val diffHoras = TimeUnit.MILLISECONDS.toHours(agora - data)

        frame.addView(card)
        if (diffHoras < 24) {
            frame.addView(dot)
        }

        return frame
    }

    private fun tempoRelativo(data: Long): String {
        val agora = System.currentTimeMillis()
        val diff = agora - data
        val minutos = TimeUnit.MILLISECONDS.toMinutes(diff)
        val horas = TimeUnit.MILLISECONDS.toHours(diff)
        val dias = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutos < 1 -> "Agora mesmo"
            minutos < 60 -> "$minutos minutos atrás"
            horas < 24 -> "$horas horas atrás"
            dias == 1L -> "Ontem"
            else -> "$dias dias atrás"
        }
    }
}

package com.example.unifood

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class PedidosActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var lojaId: String = ""
    private var pedidoId: String = ""
    private var listenerPedido: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        lojaId   = intent.getStringExtra("lojaId")   ?: ""
        pedidoId = intent.getStringExtra("pedidoId") ?: ""

        val navInicio  = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil  = findViewById<LinearLayout>(R.id.navPerfil)

        val indicatorInicio  = findViewById<View>(R.id.indicatorInicio)
        val indicatorPedidos = findViewById<View>(R.id.indicatorPedidos)
        val indicatorPerfil  = findViewById<View>(R.id.indicatorPerfil)

        navInicio.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navPedidos.setOnClickListener {
            indicatorInicio.setBackgroundColor(Color.TRANSPARENT)
            indicatorPedidos.setBackgroundResource(R.color.orange)
            indicatorPerfil.setBackgroundColor(Color.TRANSPARENT)
        }

        navPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.btnCancelar).setOnClickListener {
            showCancelarDialog()
        }

        if (pedidoId.isNotEmpty()) {
            ouvirPedido()
        }

        showAvaliacaoDialog()
    }

    private fun ouvirPedido() {
        listenerPedido = db.collection("pedidos").document(pedidoId)
            .addSnapshotListener { doc, _ ->
                if (doc == null || !doc.exists()) return@addSnapshotListener

                val codigo  = doc.getString("codigo") ?: ""
                val status  = doc.getString("status") ?: "recebido"
                val total   = doc.getDouble("total") ?: 0.0

                findViewById<TextView>(R.id.tvCodigoPedido).text = "#$codigo"
                findViewById<TextView>(R.id.tvTotalPedido).text  = "R$ %.2f".format(total)

                atualizarTimeline(status)
                atualizarResumo(doc)
            }
    }

    private fun atualizarTimeline(status: String) {
        val corVerde   = Color.parseColor("#4CAF50")
        val corLaranja = Color.parseColor("#FF6B00")
        val corCinza   = Color.parseColor("#CCCCCC")
        val corDivisor = Color.parseColor("#DDDDDD")

        // Pontos da timeline
        val p1 = findViewById<View>(R.id.ponto1)
        val p2 = findViewById<View>(R.id.ponto2)
        val p3 = findViewById<View>(R.id.ponto3)
        val p4 = findViewById<View>(R.id.ponto4)

        // Linhas da timeline
        val l1 = findViewById<View>(R.id.linha1)
        val l2 = findViewById<View>(R.id.linha2)
        val l3 = findViewById<View>(R.id.linha3)

        // Textos de status
        val tvStatus1 = findViewById<TextView>(R.id.tvStatus1)
        val tvStatus2 = findViewById<TextView>(R.id.tvStatus2)
        val tvStatus3 = findViewById<TextView>(R.id.tvStatus3)
        val tvStatus4 = findViewById<TextView>(R.id.tvStatus4)

        when (status) {
            "recebido" -> {
                p1.setBackgroundColor(corVerde)
                l1.setBackgroundColor(corDivisor)
                p2.setBackgroundColor(corCinza)
                l2.setBackgroundColor(corDivisor)
                p3.setBackgroundColor(corCinza)
                l3.setBackgroundColor(corDivisor)
                p4.setBackgroundColor(corCinza)
                tvStatus1.setTextColor(Color.BLACK)
                tvStatus2.setTextColor(Color.parseColor("#BBBBBB"))
                tvStatus3.setTextColor(Color.parseColor("#BBBBBB"))
                tvStatus4.setTextColor(Color.parseColor("#BBBBBB"))
            }
            "preparando" -> {
                p1.setBackgroundColor(corVerde)
                l1.setBackgroundColor(corVerde)
                p2.setBackgroundColor(corVerde)
                l2.setBackgroundColor(corDivisor)
                p3.setBackgroundColor(corCinza)
                l3.setBackgroundColor(corDivisor)
                p4.setBackgroundColor(corCinza)
                tvStatus1.setTextColor(Color.BLACK)
                tvStatus2.setTextColor(Color.BLACK)
                tvStatus3.setTextColor(Color.parseColor("#BBBBBB"))
                tvStatus4.setTextColor(Color.parseColor("#BBBBBB"))
            }
            "pronto" -> {
                p1.setBackgroundColor(corVerde)
                l1.setBackgroundColor(corVerde)
                p2.setBackgroundColor(corVerde)
                l2.setBackgroundColor(corVerde)
                p3.setBackgroundColor(corLaranja)
                l3.setBackgroundColor(corDivisor)
                p4.setBackgroundColor(corCinza)
                tvStatus1.setTextColor(Color.BLACK)
                tvStatus2.setTextColor(Color.BLACK)
                tvStatus3.setTextColor(corLaranja)
                tvStatus4.setTextColor(Color.parseColor("#BBBBBB"))
            }
            "entregue" -> {
                p1.setBackgroundColor(corVerde)
                l1.setBackgroundColor(corVerde)
                p2.setBackgroundColor(corVerde)
                l2.setBackgroundColor(corVerde)
                p3.setBackgroundColor(corVerde)
                l3.setBackgroundColor(corVerde)
                p4.setBackgroundColor(corVerde)
                tvStatus1.setTextColor(Color.BLACK)
                tvStatus2.setTextColor(Color.BLACK)
                tvStatus3.setTextColor(Color.BLACK)
                tvStatus4.setTextColor(Color.BLACK)
            }
        }
    }

    private fun atualizarResumo(doc: com.google.firebase.firestore.DocumentSnapshot) {
        val container = findViewById<LinearLayout>(R.id.containerResumo)
        container.removeAllViews()

        val itens = doc.get("itens") as? List<Map<String, Any>> ?: return

        itens.forEach { item ->
            val nome       = item["nome"] as? String ?: ""
            val preco      = (item["preco"] as? Double) ?: 0.0
            val quantidade = (item["quantidade"] as? Long)?.toInt() ?: 1

            val tv = TextView(this)
            tv.text      = "${quantidade}x $nome    R$ %.2f".format(preco * quantidade)
            tv.textSize  = 14f
            tv.setTextColor(Color.BLACK)
            container.addView(tv)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerPedido?.remove()
    }

    private fun showAvaliacaoDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_avaliacao)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        dialog.findViewById<View>(R.id.btnEnviar).setOnClickListener {
            val nota       = dialog.findViewById<RatingBar>(R.id.ratingBar).rating
            val comentario = dialog.findViewById<EditText>(R.id.etComentario).text.toString().trim()
            val uid        = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            if (lojaId.isEmpty()) {
                Toast.makeText(this, "Erro: loja não identificada", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val avaliacao = hashMapOf(
                "usuarioUid" to uid,
                "lojaId"     to lojaId,
                "nota"       to nota,
                "comentario" to comentario,
                "data"       to System.currentTimeMillis()
            )

            FirebaseFirestore.getInstance()
                .collection("avaliacoes")
                .add(avaliacao)
                .addOnSuccessListener {
                    Toast.makeText(this, "Avaliação enviada!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao enviar avaliação", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) dialog.dismiss()
        }, 10000)
    }

    private fun showCancelarDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_cancelar)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        dialog.findViewById<View>(R.id.btnVoltar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
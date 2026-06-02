package com.example.unifood

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class PedidosActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var lojaId: String = ""
    private var pedidoId: String = ""
    private var listenerPedido: ListenerRegistration? = null
    private var statusAnterior: String = ""
    private var avaliacaoMostrada = false
    private var primeiraLeitura = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        criarCanalNotificacao()

        lojaId   = intent.getStringExtra("lojaId")   ?: ""
        pedidoId = intent.getStringExtra("pedidoId") ?: ""

        val navInicio  = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil  = findViewById<LinearLayout>(R.id.navPerfil)

        navInicio.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navPedidos.setOnClickListener { }

        navPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.btnCancelar).setOnClickListener {
            showCancelarDialog()
        }

        if (pedidoId.isNotEmpty()) {
            ouvirPedido()
        } else {
            buscarUltimoPedido()
        }
    }

    private fun buscarUltimoPedido() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("pedidos")
            .whereEqualTo("usuarioUid", uid)
            .get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) return@addOnSuccessListener

                val pedidoMaisRecente = resultado.documents
                    .sortedByDescending { it.getLong("data") ?: 0L }
                    .first()

                pedidoId = pedidoMaisRecente.id
                lojaId = pedidoMaisRecente.getString("lojaId") ?: ""
                ouvirPedido()
            }
    }

    private fun ouvirPedido() {
        listenerPedido = db.collection("pedidos").document(pedidoId)
            .addSnapshotListener { doc, _ ->
                if (doc == null || !doc.exists()) return@addSnapshotListener

                val codigo = doc.getString("codigo") ?: ""
                val status = doc.getString("status") ?: "recebido"
                val total  = doc.getDouble("total") ?: 0.0

                if (lojaId.isEmpty()) {
                    lojaId = doc.getString("lojaId") ?: ""
                }

                findViewById<TextView>(R.id.tvCodigoPedido).text = "#$codigo"
                findViewById<TextView>(R.id.tvTotalPedido).text  = "R$ %.2f".format(total)

                atualizarTimeline(status)
                atualizarResumo(doc)

                if (!primeiraLeitura && status != statusAnterior) {
                    enviarNotificacaoStatus(status)
                }
                primeiraLeitura = false

                if (status == "entregue" && statusAnterior != "entregue" && !avaliacaoMostrada) {
                    avaliacaoMostrada = true
                    showAvaliacaoDialog()
                }

                if (status == "entregue" || status == "cancelado") {
                    findViewById<View>(R.id.btnCancelar).visibility = View.GONE
                } else {
                    findViewById<View>(R.id.btnCancelar).visibility = View.VISIBLE
                }

                statusAnterior = status
            }
    }

    private fun enviarNotificacaoStatus(status: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                val notifStatusPedido = doc.getBoolean("notif_status_pedido") ?: true
                if (!notifStatusPedido) return@addOnSuccessListener

                val mensagem = when (status) {
                    "preparando" -> "Seu pedido está sendo preparado!"
                    "pronto" -> "Seu pedido está pronto para retirada!"
                    "entregue" -> "Seu pedido foi entregue!"
                    "cancelado" -> "Seu pedido foi cancelado."
                    else -> return@addOnSuccessListener
                }

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notification = NotificationCompat.Builder(this, "pedidos_channel")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Unifood - Pedido")
                    .setContentText(mensagem)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(System.currentTimeMillis().toInt(), notification)
            }
    }

    private fun criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "pedidos_channel",
                "Pedidos",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun atualizarTimeline(status: String) {
        val corVerde   = Color.parseColor("#4CAF50")
        val corLaranja = Color.parseColor("#FF6B00")
        val corDivisor = Color.parseColor("#DDDDDD")

        val p1 = findViewById<TextView>(R.id.ponto1)
        val p2 = findViewById<TextView>(R.id.ponto2)
        val p3 = findViewById<TextView>(R.id.ponto3)
        val p4 = findViewById<TextView>(R.id.ponto4)

        val l1 = findViewById<View>(R.id.linha1)
        val l2 = findViewById<View>(R.id.linha2)
        val l3 = findViewById<View>(R.id.linha3)

        val tvStatus1 = findViewById<TextView>(R.id.tvStatus1)
        val tvStatus2 = findViewById<TextView>(R.id.tvStatus2)
        val tvStatus3 = findViewById<TextView>(R.id.tvStatus3)
        val tvStatus4 = findViewById<TextView>(R.id.tvStatus4)

        when (status) {
            "recebido" -> {
                p1.setBackgroundResource(R.drawable.bg_circle_green)
                l1.setBackgroundColor(corDivisor)
                p2.setBackgroundResource(R.drawable.bg_circle_gray)
                l2.setBackgroundColor(corDivisor)
                p3.setBackgroundResource(R.drawable.bg_circle_gray)
                l3.setBackgroundColor(corDivisor)
                p4.setBackgroundResource(R.drawable.bg_circle_gray)
                tvStatus1.setTextColor(Color.BLACK)
                tvStatus2.setTextColor(Color.parseColor("#BBBBBB"))
                tvStatus3.setTextColor(Color.parseColor("#BBBBBB"))
                tvStatus4.setTextColor(Color.parseColor("#BBBBBB"))
            }
            "preparando" -> {
                p1.setBackgroundResource(R.drawable.bg_circle_green)
                l1.setBackgroundColor(corVerde)
                p2.setBackgroundResource(R.drawable.bg_circle_green)
                l2.setBackgroundColor(corDivisor)
                p3.setBackgroundResource(R.drawable.bg_circle_gray)
                l3.setBackgroundColor(corDivisor)
                p4.setBackgroundResource(R.drawable.bg_circle_gray)
                tvStatus1.setTextColor(Color.BLACK)
                tvStatus2.setTextColor(Color.BLACK)
                tvStatus3.setTextColor(Color.parseColor("#BBBBBB"))
                tvStatus4.setTextColor(Color.parseColor("#BBBBBB"))
            }
            "pronto" -> {
                p1.setBackgroundResource(R.drawable.bg_circle_green)
                l1.setBackgroundColor(corVerde)
                p2.setBackgroundResource(R.drawable.bg_circle_green)
                l2.setBackgroundColor(corVerde)
                p3.setBackgroundResource(R.drawable.bg_circle_green)
                l3.setBackgroundColor(corDivisor)
                p4.setBackgroundResource(R.drawable.bg_circle_gray)
                tvStatus1.setTextColor(Color.BLACK)
                tvStatus2.setTextColor(Color.BLACK)
                tvStatus3.setTextColor(corLaranja)
                tvStatus4.setTextColor(Color.parseColor("#BBBBBB"))
            }
            "entregue" -> {
                p1.setBackgroundResource(R.drawable.bg_circle_green)
                l1.setBackgroundColor(corVerde)
                p2.setBackgroundResource(R.drawable.bg_circle_green)
                l2.setBackgroundColor(corVerde)
                p3.setBackgroundResource(R.drawable.bg_circle_green)
                l3.setBackgroundColor(corVerde)
                p4.setBackgroundResource(R.drawable.bg_circle_green)
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
        dialog.setCancelable(false)

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
                "pedidoId"   to pedidoId,
                "nota"       to nota,
                "comentario" to comentario,
                "data"       to System.currentTimeMillis()
            )

            db.collection("avaliacoes")
                .add(avaliacao)
                .addOnSuccessListener {
                    db.collection("pedidos").document(pedidoId)
                        .update("avaliado", true)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Avaliação enviada!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            startActivity(Intent(this, CarrinhoActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao enviar avaliação", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }

    private fun showCancelarDialog() {
        if (statusAnterior == "recebido") {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_confirmar_cancelar)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(true)

            dialog.findViewById<View>(R.id.btnNao).setOnClickListener {
                dialog.dismiss()
            }

            dialog.findViewById<View>(R.id.btnSim).setOnClickListener {
                listenerPedido?.remove()
                db.collection("pedidos").document(pedidoId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Pedido cancelado", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        startActivity(Intent(this, CarrinhoActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao cancelar pedido", Toast.LENGTH_SHORT).show()
                    }
            }

            dialog.show()
        } else {
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
}

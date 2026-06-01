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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PedidosActivity : AppCompatActivity() {

    private var lojaId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        lojaId = intent.getStringExtra("lojaId") ?: ""

        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        val indicatorInicio = findViewById<View>(R.id.indicatorInicio)
        val indicatorPedidos = findViewById<View>(R.id.indicatorPedidos)
        val indicatorPerfil = findViewById<View>(R.id.indicatorPerfil)

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

        val btnCancelar = findViewById<View>(R.id.btnCancelar)
        btnCancelar.setOnClickListener {
            showCancelarDialog()
        }

        showAvaliacaoDialog()
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
            val nota = dialog.findViewById<RatingBar>(R.id.ratingBar).rating
            val comentario = dialog.findViewById<EditText>(R.id.etComentario).text.toString().trim()
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            if (lojaId.isEmpty()) {
                Toast.makeText(this, "Erro: loja não identificada", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val avaliacao = hashMapOf(
                "usuarioUid" to uid,
                "lojaId" to lojaId,
                "nota" to nota,
                "comentario" to comentario,
                "data" to System.currentTimeMillis()
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
            if (dialog.isShowing) {
                dialog.dismiss()
            }
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
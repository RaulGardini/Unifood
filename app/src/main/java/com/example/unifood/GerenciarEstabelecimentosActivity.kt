package com.example.unifood

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GerenciarEstabelecimentosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerenciar_estabelecimentos)

        val btnDelete1 = findViewById<ImageButton>(R.id.btnDelete1)
        val btnDelete2 = findViewById<ImageButton>(R.id.btnDelete2)
        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val btnAdicionar = findViewById<View>(R.id.btnAdicionar)

        btnDelete1.setOnClickListener {
            mostrarPopup()
        }

        btnDelete2.setOnClickListener {
            mostrarPopup()
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        btnAdicionar.setOnClickListener {
            val intent = Intent(this, NovoEstabelecimentoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_excluir_estabelecimento)
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
            Toast.makeText(this, "Estabelecimento excluído!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}

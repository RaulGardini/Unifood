package com.example.unifood

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CardapioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardapio)

        val btnAdicionarCardapio = findViewById<Button>(R.id.btnAdicionarCardapio)
        val btnAddCategoria = findViewById<TextView>(R.id.btnAddCategoria)

        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)
        val navCardapio = findViewById<LinearLayout>(R.id.navCardapio)

        btnAdicionarCardapio.setOnClickListener {
            val intent = Intent(this, NovoItemCardapioActivity::class.java)
            startActivity(intent)
        }

        val btnEditarItem = findViewById<TextView>(R.id.btnEditarItem)
        val btnExcluirItem1 = findViewById<TextView>(R.id.btnExcluirItem1)
        val btnExcluirItem2 = findViewById<TextView>(R.id.btnExcluirItem2)

        btnEditarItem.setOnClickListener {
            val intent = Intent(this, EditarItemCardapioActivity::class.java)
            startActivity(intent)
        }

        btnExcluirItem1.setOnClickListener {
            mostrarDialogExcluir("Kalzone de frango")
        }

        btnExcluirItem2.setOnClickListener {
            mostrarDialogExcluir("Kalzone de carne")
        }

        btnAddCategoria.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_nova_categoria)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(true)

            dialog.findViewById<View>(R.id.btnVoltar).setOnClickListener {
                dialog.dismiss()
            }

            dialog.findViewById<View>(R.id.btnAdicionarCategoria).setOnClickListener {
                val nome = dialog.findViewById<EditText>(R.id.etNomeCategoria).text.toString().trim()
                if (nome.isNotEmpty()) {
                    Toast.makeText(this, "Categoria '$nome' adicionada!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            dialog.show()
        }

        navDashboard.setOnClickListener {
            val intent = Intent(this, LojistaActivity::class.java)
            startActivity(intent)
            finish()
        }

        navCardapio.setOnClickListener {
        }
    }

    private fun mostrarDialogExcluir(nomeItem: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_excluir_item)
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
            Toast.makeText(this, "'$nomeItem' excluído do cardápio!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}

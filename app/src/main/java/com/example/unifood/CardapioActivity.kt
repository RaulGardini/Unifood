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

        btnEditarItem.setOnClickListener {
            val intent = Intent(this, EditarItemCardapioActivity::class.java)
            startActivity(intent)
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
}

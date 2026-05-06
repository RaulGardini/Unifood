package com.example.unifood

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
            val builder = AlertDialog.Builder(this)
            val input = EditText(this)
            input.hint = "Digite o nome da nova categoria:"
            input.setPadding(40, 30, 40, 30)

            builder.setTitle("Nova categoria")
            builder.setView(input)

            builder.setPositiveButton("Adicionar categoria") { dialog, _ ->
                val nome = input.text.toString().trim()
                if (nome.isNotEmpty()) {
                    Toast.makeText(this, "Categoria '$nome' adicionada!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Voltar") { dialog, _ ->
                dialog.dismiss()
            }

            builder.create().show()
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

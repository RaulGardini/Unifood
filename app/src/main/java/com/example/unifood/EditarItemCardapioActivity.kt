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

class EditarItemCardapioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_item_cardapio)

        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val btnAddCategoria = findViewById<TextView>(R.id.btnAddCategoria)
        val btnDisponivel = findViewById<TextView>(R.id.btnDisponivel)
        val btnIndisponivel = findViewById<TextView>(R.id.btnIndisponivel)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)
        val navCardapio = findViewById<LinearLayout>(R.id.navCardapio)

        btnVoltar.setOnClickListener { finish() }

        btnSalvar.setOnClickListener {
            Toast.makeText(this, "Item salvo!", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnDisponivel.setOnClickListener {
            btnDisponivel.setBackgroundResource(R.drawable.bg_disponivel)
            btnDisponivel.setTextColor(resources.getColor(R.color.white, null))
            btnIndisponivel.setBackgroundResource(R.drawable.bg_tag_unselect)
            btnIndisponivel.setTextColor(resources.getColor(R.color.text_dark, null))
        }

        btnIndisponivel.setOnClickListener {
            btnIndisponivel.setBackgroundResource(R.drawable.bg_indisponivel)
            btnIndisponivel.setTextColor(resources.getColor(R.color.white, null))
            btnDisponivel.setBackgroundResource(R.drawable.bg_tag_unselect)
            btnDisponivel.setTextColor(resources.getColor(R.color.text_dark, null))
        }

        btnAddCategoria.setOnClickListener {
            val input = EditText(this)
            input.hint = "Digite o nome da nova categoria:"
            input.setPadding(40, 30, 40, 30)

            AlertDialog.Builder(this)
                .setTitle("Nova categoria")
                .setView(input)
                .setPositiveButton("Adicionar categoria") { dialog, _ ->
                    val nome = input.text.toString().trim()
                    if (nome.isNotEmpty()) {
                        Toast.makeText(this, "Categoria '$nome' adicionada!", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Voltar") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        navDashboard.setOnClickListener {
            startActivity(Intent(this, LojistaActivity::class.java))
            finish()
        }

        navCardapio.setOnClickListener {
            startActivity(Intent(this, CardapioActivity::class.java))
            finish()
        }
    }
}

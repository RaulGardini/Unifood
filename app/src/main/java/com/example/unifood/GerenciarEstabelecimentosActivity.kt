package com.example.unifood



import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GerenciarEstabelecimentosActivity : AppCompatActivity() {
    private lateinit var btnVoltar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerenciar_estabelecimentos)

        val btnDelete1 = findViewById<ImageButton>(R.id.btnDelete1)
        val btnDelete2 = findViewById<ImageButton>(R.id.btnDelete2)
        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)


        btnDelete1.setOnClickListener {
            mostrarPopup()
        }

        btnDelete2.setOnClickListener {
            mostrarPopup()
        }
        btnVoltar.setOnClickListener() {
            finish()
        }

    }

    private fun mostrarPopup() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Excluir")
        builder.setMessage("Tem certeza de que quer excluir esse estabelecimento?")

        // Botão NÃO
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }

        // Botão SIM
        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
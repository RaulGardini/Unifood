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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NovoItemCardapioActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var estabelecimentoId: String? = null
    private var categoriaSelecionada = "Lanches"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_item_cardapio)

        estabelecimentoId = intent.getStringExtra("estabelecimentoId")

        // Se não veio pelo intent, busca do Firestore
        if (estabelecimentoId == null) {
            val uid = auth.currentUser?.uid ?: return
            db.collection("estabelecimentos").whereEqualTo("donoUid", uid).get()
                .addOnSuccessListener { resultado ->
                    if (!resultado.isEmpty) {
                        estabelecimentoId = resultado.documents[0].id
                    }
                }
        }

        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val btnAdicionar = findViewById<Button>(R.id.btnAdicionar)
        val btnAddCategoria = findViewById<TextView>(R.id.btnAddCategoria)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)
        val navCardapio = findViewById<LinearLayout>(R.id.navCardapio)
        val edtNome = findViewById<EditText>(R.id.edtNome)
        val edtDescricao = findViewById<EditText>(R.id.edtDescricao)
        val edtValor = findViewById<EditText>(R.id.edtValor)

        btnVoltar.setOnClickListener { finish() }

        btnAdicionar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val descricao = edtDescricao.text.toString().trim()
            val valorTexto = edtValor.text.toString().trim()

            if (nome.isEmpty()) {
                edtNome.error = "Digite o nome"
                return@setOnClickListener
            }
            if (valorTexto.isEmpty()) {
                edtValor.error = "Digite o valor"
                return@setOnClickListener
            }

            val preco = valorTexto.toDoubleOrNull() ?: 0.0
            val id = estabelecimentoId
            if (id == null) {
                Toast.makeText(this, "Erro: estabelecimento não encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val item = hashMapOf(
                "nome" to nome,
                "descricao" to descricao,
                "preco" to preco,
                "categoria" to categoriaSelecionada,
                "disponivel" to true
            )

            btnAdicionar.isEnabled = false

            db.collection("estabelecimentos").document(id)
                .collection("cardapio").add(item)
                .addOnSuccessListener {
                    Toast.makeText(this, "Item adicionado ao cardápio!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao adicionar item", Toast.LENGTH_SHORT).show()
                    btnAdicionar.isEnabled = true
                }
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
            startActivity(Intent(this, LojistaActivity::class.java))
            finish()
        }

        navCardapio.setOnClickListener {
            startActivity(Intent(this, CardapioActivity::class.java))
            finish()
        }
    }
}

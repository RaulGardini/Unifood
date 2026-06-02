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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NovoItemCardapioActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var estabelecimentoId: String? = null
    private var categoriaSelecionada: String = ""

    private lateinit var containerCategorias: LinearLayout
    private lateinit var btnAddCategoria: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_item_cardapio)

        estabelecimentoId = intent.getStringExtra("estabelecimentoId")
        containerCategorias = findViewById(R.id.containerCategorias)
        btnAddCategoria = findViewById(R.id.btnAddCategoria)

        if (estabelecimentoId == null) {
            val uid = auth.currentUser?.uid ?: return
            db.collection("estabelecimentos").whereEqualTo("donoUid", uid).get()
                .addOnSuccessListener { resultado ->
                    if (!resultado.isEmpty) {
                        estabelecimentoId = resultado.documents[0].id
                        carregarCategorias()
                    }
                }
        } else {
            carregarCategorias()
        }

        findViewById<TextView>(R.id.btnVoltar).setOnClickListener { finish() }

        btnAddCategoria.setOnClickListener { mostrarDialogCategoria() }

        findViewById<Button>(R.id.btnAdicionar).setOnClickListener { adicionar() }

        findViewById<LinearLayout>(R.id.navDashboard).setOnClickListener {
            startActivity(Intent(this, LojistaActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navCardapio).setOnClickListener {
            startActivity(Intent(this, CardapioActivity::class.java))
            finish()
        }
    }

    private fun carregarCategorias() {
        val id = estabelecimentoId ?: return
        db.collection("estabelecimentos").document(id).get()
            .addOnSuccessListener { doc ->
                val categorias = doc.get("categorias") as? List<*> ?: emptyList<String>()
                containerCategorias.removeAllViews()

                for (cat in categorias) {
                    val nome = cat.toString()
                    val tv = TextView(this).apply {
                        text = nome
                        textSize = 13f
                        setPadding(32, 0, 32, 0)
                        gravity = android.view.Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, 85
                        ).apply { marginEnd = 16 }
                    }

                    if (categoriaSelecionada == nome) {
                        tv.setBackgroundResource(R.drawable.bg_tab_active)
                        tv.setTextColor(Color.WHITE)
                    } else {
                        tv.setBackgroundResource(R.drawable.bg_tab_inactive)
                        tv.setTextColor(0xFF333333.toInt())
                    }

                    tv.setOnClickListener {
                        categoriaSelecionada = nome
                        carregarCategorias()
                    }

                    containerCategorias.addView(tv)
                }

                containerCategorias.addView(btnAddCategoria)
            }
    }

    private fun adicionar() {
        val edtNome = findViewById<EditText>(R.id.edtNome)
        val edtDescricao = findViewById<EditText>(R.id.edtDescricao)
        val edtValor = findViewById<EditText>(R.id.edtValor)
        val btnAdicionar = findViewById<Button>(R.id.btnAdicionar)

        val nome = edtNome.text.toString().trim()
        val descricao = edtDescricao.text.toString().trim()
        val valorTexto = edtValor.text.toString().trim().replace(",", ".")

        if (nome.isEmpty()) {
            edtNome.error = "Digite o nome"
            return
        }
        if (valorTexto.isEmpty()) {
            edtValor.error = "Digite o valor"
            return
        }
        if (categoriaSelecionada.isEmpty()) {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show()
            return
        }

        val preco = valorTexto.toDoubleOrNull() ?: 0.0
        val id = estabelecimentoId
        if (id == null) {
            Toast.makeText(this, "Erro: estabelecimento não encontrado", Toast.LENGTH_SHORT).show()
            return
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

    private fun mostrarDialogCategoria() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_nova_categoria)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        dialog.findViewById<View>(R.id.btnVoltar).setOnClickListener { dialog.dismiss() }

        dialog.findViewById<View>(R.id.btnAdicionarCategoria).setOnClickListener {
            val nome = dialog.findViewById<EditText>(R.id.etNomeCategoria).text.toString().trim()
            val id = estabelecimentoId

            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (id == null) {
                Toast.makeText(this, "Estabelecimento não encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("estabelecimentos").document(id)
                .update("categorias", FieldValue.arrayUnion(nome))
                .addOnSuccessListener {
                    Toast.makeText(this, "Categoria '$nome' adicionada!", Toast.LENGTH_SHORT).show()
                    categoriaSelecionada = nome
                    carregarCategorias()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao adicionar categoria", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }
}

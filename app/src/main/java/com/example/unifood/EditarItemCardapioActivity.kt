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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EditarItemCardapioActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private var estabelecimentoId: String = ""
    private var itemId: String = ""
    private var categoriaSelecionada: String = ""
    private var disponivel: Boolean = true

    private lateinit var edtNome: EditText
    private lateinit var edtDescricao: EditText
    private lateinit var edtValor: EditText
    private lateinit var btnDisponivel: TextView
    private lateinit var btnIndisponivel: TextView
    private lateinit var containerCategorias: LinearLayout
    private lateinit var btnAddCategoria: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_item_cardapio)

        estabelecimentoId = intent.getStringExtra("estabelecimentoId") ?: ""
        itemId = intent.getStringExtra("itemId") ?: ""

        edtNome = findViewById(R.id.edtNome)
        edtDescricao = findViewById(R.id.edtDescricao)
        edtValor = findViewById(R.id.edtValor)
        btnDisponivel = findViewById(R.id.btnDisponivel)
        btnIndisponivel = findViewById(R.id.btnIndisponivel)
        containerCategorias = findViewById(R.id.containerCategorias)
        btnAddCategoria = findViewById(R.id.btnAddCategoria)

        findViewById<TextView>(R.id.btnVoltar).setOnClickListener { finish() }

        btnAddCategoria.setOnClickListener { mostrarDialogCategoria() }

        findViewById<Button>(R.id.btnSalvar).setOnClickListener { salvar() }

        btnDisponivel.setOnClickListener { setDisponibilidade(true) }
        btnIndisponivel.setOnClickListener { setDisponibilidade(false) }

        findViewById<LinearLayout>(R.id.navDashboard).setOnClickListener {
            startActivity(Intent(this, LojistaActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navCardapio).setOnClickListener {
            startActivity(Intent(this, CardapioActivity::class.java))
            finish()
        }

        carregarCategorias()
        carregarItem()
    }

    private fun carregarCategorias() {
        if (estabelecimentoId.isEmpty()) return
        db.collection("estabelecimentos").document(estabelecimentoId).get()
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

                    atualizarEstiloCategoria(tv, nome)

                    tv.setOnClickListener {
                        categoriaSelecionada = nome
                        carregarCategorias()
                    }

                    containerCategorias.addView(tv)
                }

                containerCategorias.addView(btnAddCategoria)
            }
    }

    private fun atualizarEstiloCategoria(tv: TextView, nome: String) {
        if (categoriaSelecionada == nome) {
            tv.setBackgroundResource(R.drawable.bg_tab_active)
            tv.setTextColor(Color.WHITE)
        } else {
            tv.setBackgroundResource(R.drawable.bg_tab_inactive)
            tv.setTextColor(0xFF333333.toInt())
        }
    }

    private fun carregarItem() {
        if (estabelecimentoId.isEmpty() || itemId.isEmpty()) return

        db.collection("estabelecimentos").document(estabelecimentoId)
            .collection("cardapio").document(itemId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                edtNome.setText(doc.getString("nome") ?: "")
                edtDescricao.setText(doc.getString("descricao") ?: "")
                val preco = doc.getDouble("preco") ?: 0.0
                edtValor.setText("%.2f".format(preco))

                categoriaSelecionada = doc.getString("categoria") ?: ""
                disponivel = doc.getBoolean("disponivel") ?: true

                setDisponibilidade(disponivel)
                carregarCategorias()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar item", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setDisponibilidade(disp: Boolean) {
        disponivel = disp
        if (disp) {
            btnDisponivel.setBackgroundResource(R.drawable.bg_disponivel)
            btnDisponivel.setTextColor(Color.WHITE)
            btnIndisponivel.setBackgroundResource(R.drawable.bg_tag_unselect)
            btnIndisponivel.setTextColor(resources.getColor(R.color.text_dark, null))
        } else {
            btnIndisponivel.setBackgroundResource(R.drawable.bg_indisponivel)
            btnIndisponivel.setTextColor(Color.WHITE)
            btnDisponivel.setBackgroundResource(R.drawable.bg_tag_unselect)
            btnDisponivel.setTextColor(resources.getColor(R.color.text_dark, null))
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

            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (estabelecimentoId.isEmpty()) {
                Toast.makeText(this, "Estabelecimento não encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("estabelecimentos").document(estabelecimentoId)
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

    private fun salvar() {
        val nome = edtNome.text.toString().trim()
        val descricao = edtDescricao.text.toString().trim()
        val valorTexto = edtValor.text.toString().trim().replace(",", ".")

        if (nome.isEmpty()) {
            edtNome.error = "Digite o nome"
            return
        }

        val preco = valorTexto.toDoubleOrNull()
        if (preco == null) {
            edtValor.error = "Valor inválido"
            return
        }

        if (categoriaSelecionada.isEmpty()) {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show()
            return
        }

        val dados = mapOf(
            "nome" to nome,
            "descricao" to descricao,
            "preco" to preco,
            "categoria" to categoriaSelecionada,
            "disponivel" to disponivel
        )

        db.collection("estabelecimentos").document(estabelecimentoId)
            .collection("cardapio").document(itemId)
            .update(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Item atualizado!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show()
            }
    }
}

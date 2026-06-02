package com.example.unifood

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CardapioActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var estabelecimentoId: String? = null
    private var filtroCategoria: String? = null

    private lateinit var containerItens: LinearLayout
    private lateinit var containerCategorias: LinearLayout
    private lateinit var tvVazio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardapio)

        containerItens = findViewById(R.id.containerItens)
        containerCategorias = findViewById(R.id.containerCategorias)
        tvVazio = findViewById(R.id.tvVazio)

        buscarEstabelecimentoECarregarItens()

        findViewById<Button>(R.id.btnAdicionarCardapio).setOnClickListener {
            val intent = Intent(this, NovoItemCardapioActivity::class.java)
            intent.putExtra("estabelecimentoId", estabelecimentoId)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnAddCategoria).setOnClickListener {
            mostrarDialogCategoria()
        }

        findViewById<LinearLayout>(R.id.navDashboard).setOnClickListener {
            startActivity(Intent(this, LojistaActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navCardapio).setOnClickListener { }
    }

    override fun onResume() {
        super.onResume()
        if (estabelecimentoId != null) {
            carregarCategorias()
            carregarItens()
        }
    }

    private fun buscarEstabelecimentoECarregarItens() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("estabelecimentos").whereEqualTo("donoUid", uid).get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) return@addOnSuccessListener
                estabelecimentoId = resultado.documents[0].id
                carregarCategorias()
                carregarItens()
            }
    }

    private fun carregarCategorias() {
        val id = estabelecimentoId ?: return
        db.collection("estabelecimentos").document(id).get()
            .addOnSuccessListener { doc ->
                val categorias = doc.get("categorias") as? List<*> ?: emptyList<String>()

                val btnAdd = findViewById<TextView>(R.id.btnAddCategoria)
                containerCategorias.removeAllViews()

                for (cat in categorias) {
                    val nome = cat.toString()
                    val tv = TextView(this)
                    tv.text = nome
                    tv.textSize = 13f
                    tv.setPadding(32, 0, 32, 0)
                    tv.gravity = android.view.Gravity.CENTER
                    tv.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, 85
                    ).apply { marginEnd = 16 }

                    if (filtroCategoria == nome) {
                        tv.setBackgroundResource(R.drawable.bg_tab_active)
                        tv.setTextColor(Color.WHITE)
                    } else {
                        tv.setBackgroundResource(R.drawable.bg_tab_inactive)
                        tv.setTextColor(0xFF333333.toInt())
                    }

                    tv.setOnClickListener {
                        filtroCategoria = if (filtroCategoria == nome) null else nome
                        carregarCategorias()
                        carregarItens()
                    }

                    containerCategorias.addView(tv)
                }

                containerCategorias.addView(btnAdd)
            }
    }

    private fun carregarItens() {
        val id = estabelecimentoId ?: return
        db.collection("estabelecimentos").document(id)
            .collection("cardapio").get()
            .addOnSuccessListener { resultado ->
                containerItens.removeAllViews()

                val itens = resultado.documents.map { doc ->
                    ItemCardapio(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        descricao = doc.getString("descricao") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoria = doc.getString("categoria") ?: "",
                        disponivel = doc.getBoolean("disponivel") ?: true
                    )
                }

                val itensFiltrados = if (filtroCategoria != null) {
                    itens.filter { it.categoria.equals(filtroCategoria, ignoreCase = true) }
                } else {
                    itens
                }

                if (itensFiltrados.isEmpty()) {
                    tvVazio.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                tvVazio.visibility = View.GONE
                for (item in itensFiltrados) {
                    containerItens.addView(criarCardItem(item))
                }
            }
    }

    private fun criarCardItem(item: ItemCardapio): View {
        val card = layoutInflater.inflate(R.layout.item_cardapio_lojista, containerItens, false)

        card.findViewById<TextView>(R.id.tvNomeItem).text = item.nome
        card.findViewById<TextView>(R.id.tvDescricaoItem).text = item.descricao
        card.findViewById<TextView>(R.id.tvPrecoItem).text = "R$ %.2f".format(item.preco)

        val tvDisp = card.findViewById<TextView>(R.id.tvDisponibilidade)
        if (item.disponivel) {
            tvDisp.text = "disponível"
            tvDisp.setBackgroundResource(R.drawable.bg_disponivel)
        } else {
            tvDisp.text = "indisponível"
            tvDisp.setBackgroundResource(R.drawable.bg_indisponivel)
        }

        card.findViewById<TextView>(R.id.btnEditarItem).setOnClickListener {
            val intent = Intent(this, EditarItemCardapioActivity::class.java)
            intent.putExtra("estabelecimentoId", estabelecimentoId)
            intent.putExtra("itemId", item.id)
            startActivity(intent)
        }

        card.findViewById<TextView>(R.id.btnExcluirItem).setOnClickListener {
            mostrarDialogExcluir(item)
        }

        return card
    }

    private fun mostrarDialogExcluir(item: ItemCardapio) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_excluir_item)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        dialog.findViewById<View>(R.id.btnNao).setOnClickListener { dialog.dismiss() }

        dialog.findViewById<View>(R.id.btnSim).setOnClickListener {
            val id = estabelecimentoId ?: return@setOnClickListener
            db.collection("estabelecimentos").document(id)
                .collection("cardapio").document(item.id).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "'${item.nome}' excluído!", Toast.LENGTH_SHORT).show()
                    carregarItens()
                }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogCategoria() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_nova_categoria)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

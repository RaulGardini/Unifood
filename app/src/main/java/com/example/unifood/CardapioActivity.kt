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

class CardapioActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var estabelecimentoId: String? = null

    private lateinit var containerItens: LinearLayout
    private lateinit var tvVazio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardapio)

        containerItens = findViewById(R.id.containerItens)
        tvVazio = findViewById(R.id.tvVazio)

        val btnAdicionarCardapio = findViewById<Button>(R.id.btnAdicionarCardapio)
        val btnAddCategoria = findViewById<TextView>(R.id.btnAddCategoria)
        val navDashboard = findViewById<LinearLayout>(R.id.navDashboard)
        val navCardapio = findViewById<LinearLayout>(R.id.navCardapio)

        // Busca o estabelecimento do lojista e carrega os itens
        buscarEstabelecimentoECarregarItens()

        btnAdicionarCardapio.setOnClickListener {
            val intent = Intent(this, NovoItemCardapioActivity::class.java)
            intent.putExtra("estabelecimentoId", estabelecimentoId)
            startActivity(intent)
        }

        btnAddCategoria.setOnClickListener {
            mostrarDialogCategoria()
        }

        navDashboard.setOnClickListener {
            startActivity(Intent(this, LojistaActivity::class.java))
            finish()
        }

        navCardapio.setOnClickListener {}
    }

    override fun onResume() {
        super.onResume()
        if (estabelecimentoId != null) {
            carregarItens()
        }
    }

    private fun buscarEstabelecimentoECarregarItens() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("estabelecimentos").whereEqualTo("donoUid", uid).get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) return@addOnSuccessListener
                estabelecimentoId = resultado.documents[0].id
                carregarItens()
            }
    }

    private fun carregarItens() {
        val id = estabelecimentoId ?: return
        db.collection("estabelecimentos").document(id)
            .collection("cardapio").get()
            .addOnSuccessListener { resultado ->
                containerItens.removeAllViews()

                if (resultado.isEmpty) {
                    tvVazio.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                tvVazio.visibility = View.GONE

                for (doc in resultado.documents) {
                    val item = ItemCardapio(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        descricao = doc.getString("descricao") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoria = doc.getString("categoria") ?: "",
                        disponivel = doc.getBoolean("disponivel") ?: true
                    )
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
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        dialog.findViewById<View>(R.id.btnNao).setOnClickListener {
            dialog.dismiss()
        }

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
}

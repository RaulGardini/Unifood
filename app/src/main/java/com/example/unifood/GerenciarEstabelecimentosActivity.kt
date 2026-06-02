package com.example.unifood

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore

class GerenciarEstabelecimentosActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var container: LinearLayout
    private lateinit var etBuscar: EditText

    private var todosEstabelecimentos = listOf<Triple<String, String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerenciar_estabelecimentos)

        container = findViewById(R.id.containerEstabelecimentos)
        etBuscar = findViewById(R.id.etBuscar)

        findViewById<TextView>(R.id.btnVoltar).setOnClickListener { finish() }

        findViewById<View>(R.id.btnAdicionar).setOnClickListener {
            startActivity(Intent(this, NovoEstabelecimentoActivity::class.java))
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { filtrar(s.toString()) }
        })

        carregarEstabelecimentos()
    }

    override fun onResume() {
        super.onResume()
        carregarEstabelecimentos()
    }

    private fun carregarEstabelecimentos() {
        db.collection("estabelecimentos")
            .get()
            .addOnSuccessListener { resultado ->
                todosEstabelecimentos = resultado.documents.map { doc ->
                    Triple(
                        doc.id,
                        doc.getString("nome") ?: "",
                        doc.getString("localizacao") ?: ""
                    )
                }
                filtrar(etBuscar.text.toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar estabelecimentos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filtrar(busca: String) {
        val lista = if (busca.isBlank()) todosEstabelecimentos
        else todosEstabelecimentos.filter { it.second.contains(busca, ignoreCase = true) }
        mostrarLista(lista)
    }

    private fun mostrarLista(lista: List<Triple<String, String, String>>) {
        container.removeAllViews()
        lista.forEach { (id, nome, localizacao) ->
            container.addView(criarCard(id, nome, localizacao))
        }
    }

    private fun criarCard(id: String, nome: String, localizacao: String): View {
        val card = layoutInflater.inflate(R.layout.item_estabelecimento_gerenciar, null)

        val initiais = nome.take(2).uppercase()
        card.findViewById<TextView>(R.id.tvAvatar).text = initiais
        card.findViewById<TextView>(R.id.tvNome).text = nome
        card.findViewById<TextView>(R.id.tvLocalizacao).text = localizacao

        card.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
            mostrarPopup(id)
        }

        return card
    }

    private fun mostrarPopup(estabelecimentoId: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_excluir_estabelecimento)
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
            db.collection("estabelecimentos").document(estabelecimentoId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Estabelecimento excluído!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    carregarEstabelecimentos()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
        }

        dialog.show()
    }
}
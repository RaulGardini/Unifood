package com.example.unifood

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminGenUsuarioActivity : AppCompatActivity() {

    private lateinit var btnVoltar: TextView
    private lateinit var edtBuscar: EditText
    private lateinit var containerUsuarios: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private var todosUsuarios = listOf<UsuarioAdmin>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_geren_usuario)

        btnVoltar = findViewById(R.id.btnVoltar)
        edtBuscar = findViewById(R.id.edtBuscar)
        containerUsuarios = findViewById(R.id.containerUsuarios)

        btnVoltar.setOnClickListener {
            finish()
        }

        carregarUsuarios()
        configurarBusca()
    }

    private fun carregarUsuarios() {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { resultado ->
                todosUsuarios = resultado.documents.map { doc ->
                    UsuarioAdmin(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        matricula = doc.getString("matricula") ?: "",
                        status = doc.getString("status") ?: "Ativo"
                    )
                }

                filtrarUsuarios()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar usuários", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarBusca() {
        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filtrarUsuarios()
            }
        })
    }

    private fun filtrarUsuarios() {
        val busca = edtBuscar.text.toString().trim()

        val listaFiltrada = if (busca.isBlank()) {
            todosUsuarios
        } else {
            todosUsuarios.filter {
                it.nome.contains(busca, ignoreCase = true)
            }
        }

        mostrarUsuarios(listaFiltrada)
    }

    private fun mostrarUsuarios(usuarios: List<UsuarioAdmin>) {
        containerUsuarios.removeAllViews()

        usuarios.forEach { usuario ->
            val item = layoutInflater.inflate(R.layout.item_usuario_admin, containerUsuarios, false)

            val txtIniciais = item.findViewById<TextView>(R.id.txtIniciais)
            val txtNome = item.findViewById<TextView>(R.id.txtNome)
            val txtMatricula = item.findViewById<TextView>(R.id.txtMatricula)
            val statusUser = item.findViewById<TextView>(R.id.statusUser)

            txtIniciais.text = gerarIniciais(usuario.nome)
            txtNome.text = usuario.nome
            txtMatricula.text = usuario.matricula
            statusUser.text = usuario.status

            statusUser.setOnClickListener {
                abrirPopupStatus(usuario, statusUser)
            }

            containerUsuarios.addView(item)
        }
    }

    private fun abrirPopupStatus(usuario: UsuarioAdmin, statusUser: TextView) {
        val view = LayoutInflater.from(this).inflate(R.layout.popup_usuario, null)

        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popup.elevation = 15f
        popup.isOutsideTouchable = true
        popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.findViewById<TextView>(R.id.tvAtivar).setOnClickListener {
            atualizarStatus(usuario.id, "Ativo")
            popup.dismiss()
        }

        view.findViewById<TextView>(R.id.tvDesativar).setOnClickListener {
            atualizarStatus(usuario.id, "Inativo")
            popup.dismiss()
        }

        popup.showAsDropDown(statusUser, -80, -20)
    }

    private fun atualizarStatus(usuarioId: String, novoStatus: String) {
        db.collection("usuarios")
            .document(usuarioId)
            .update("status", novoStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Status atualizado", Toast.LENGTH_SHORT).show()
                carregarUsuarios()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gerarIniciais(nome: String): String {
        val partes = nome.trim().split(" ").filter { it.isNotBlank() }

        return when {
            partes.size >= 2 -> "${partes[0][0]}${partes[1][0]}".uppercase()
            partes.size == 1 -> partes[0].take(2).uppercase()
            else -> "??"
        }
    }
}

data class UsuarioAdmin(
    val id: String = "",
    val nome: String = "",
    val matricula: String = "",
    val status: String = "Ativo"
)
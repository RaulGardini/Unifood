package com.example.unifood

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class EditarPerfilLogistaActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var btnVoltar: Button
    private lateinit var btnSalvar: Button
    private lateinit var edtNome: EditText
    private lateinit var edtLocal: EditText
    private lateinit var edtHorarioInicio: EditText
    private lateinit var edtHorarioFim: EditText

    private var estabelecimentoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editarlogistaperfil)

        btnVoltar = findViewById(R.id.btnVoltar)
        btnSalvar = findViewById(R.id.btnSalvar)
        edtNome = findViewById(R.id.edtNome)
        edtLocal = findViewById(R.id.edtLocal)
        edtHorarioInicio = findViewById(R.id.edtHorarioInicio)
        edtHorarioFim = findViewById(R.id.edtHorarioFim)

        btnVoltar.setOnClickListener {
            finish()
        }

        edtHorarioInicio.setOnClickListener {
            abrirTimePicker(edtHorarioInicio)
        }

        edtHorarioFim.setOnClickListener {
            abrirTimePicker(edtHorarioFim)
        }

        carregarDadosEstabelecimento()

        btnSalvar.setOnClickListener {
            salvarDados()
        }
    }

    private fun carregarDadosEstabelecimento() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("estabelecimentos")
            .whereEqualTo("donoUid", uid)
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->
                if (!resultado.isEmpty) {
                    val doc = resultado.documents[0]
                    estabelecimentoId = doc.id

                    edtNome.setText(doc.getString("nome") ?: "")
                    edtLocal.setText(doc.getString("localizacao") ?: "")
                    edtHorarioInicio.setText(doc.getString("horarioInicio") ?: "")
                    edtHorarioFim.setText(doc.getString("horarioFim") ?: "")
                } else {
                    Toast.makeText(this, "Nenhum estabelecimento encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarDados() {
        val id = estabelecimentoId

        if (id == null) {
            Toast.makeText(this, "Estabelecimento não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val nome = edtNome.text.toString().trim()
        val localizacao = edtLocal.text.toString().trim()
        val horarioInicio = edtHorarioInicio.text.toString().trim()
        val horarioFim = edtHorarioFim.text.toString().trim()

        if (nome.isEmpty() || localizacao.isEmpty() || horarioInicio.isEmpty() || horarioFim.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val dados = hashMapOf<String, Any>(
            "nome" to nome,
            "localizacao" to localizacao,
            "horarioInicio" to horarioInicio,
            "horarioFim" to horarioFim
        )

        db.collection("estabelecimentos")
            .document(id)
            .update(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirTimePicker(campo: EditText) {
        val dialog = TimePickerDialog(this, { _, hora, minuto ->
            campo.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, minuto))
        }, 8, 0, true)

        dialog.show()
    }
}
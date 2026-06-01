package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Calendar

class PainelAdministrativoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var btnLogout: TextView
    private lateinit var tvContUsuario: TextView
    private lateinit var tvContEstabe: TextView
    private lateinit var tvContPedidos: TextView
    private lateinit var tvContBloqueados: TextView

    private lateinit var navGerenEstabe: LinearLayout
    private lateinit var navGerenUsuario: LinearLayout
    private lateinit var navRelatorio: LinearLayout

    private var listenerUsuarios: ListenerRegistration? = null
    private var listenerEstabelecimentos: ListenerRegistration? = null
    private var listenerPedidosHoje: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paineladmin)

        btnLogout = findViewById(R.id.btnLogout)
        tvContUsuario = findViewById(R.id.tvContUsuario)
        tvContEstabe = findViewById(R.id.tvContEstabe)
        tvContPedidos = findViewById(R.id.tvContPedidos)
        tvContBloqueados = findViewById(R.id.tvContBloqueados)

        navGerenEstabe = findViewById(R.id.navGerenEstabe)
        navGerenUsuario = findViewById(R.id.navGerenUsuario)
        navRelatorio = findViewById(R.id.navRelatorio)

        configurarCliques()
    }

    override fun onStart() {
        super.onStart()
        iniciarListenersTempoReal()
    }

    override fun onStop() {
        super.onStop()
        removerListeners()
    }

    private fun iniciarListenersTempoReal() {
        carregarUsuariosTempoReal()
        carregarEstabelecimentosTempoReal()
        carregarPedidosHojeTempoReal()
    }

    private fun carregarUsuariosTempoReal() {
        listenerUsuarios = db.collection("usuarios")
            .addSnapshotListener { resultado, erro ->
                if (erro != null) {
                    tvContUsuario.text = "0"
                    tvContBloqueados.text = "0"
                    Toast.makeText(this, "Erro ao carregar usuários", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (resultado == null) {
                    tvContUsuario.text = "0"
                    tvContBloqueados.text = "0"
                    return@addSnapshotListener
                }

                tvContUsuario.text = resultado.size().toString()

                var bloqueados = 0

                for (doc in resultado.documents) {
                    val status = doc.getString("status") ?: "Ativo"

                    if (
                        status.equals("Inativo", ignoreCase = true) ||
                        status.equals("Bloqueado", ignoreCase = true)
                    ) {
                        bloqueados++
                    }
                }

                tvContBloqueados.text = bloqueados.toString()
            }
    }

    private fun carregarEstabelecimentosTempoReal() {
        listenerEstabelecimentos = db.collection("estabelecimentos")
            .addSnapshotListener { resultado, erro ->
                if (erro != null) {
                    tvContEstabe.text = "0"
                    Toast.makeText(this, "Erro ao carregar estabelecimentos", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                tvContEstabe.text = resultado?.size()?.toString() ?: "0"
            }
    }

    private fun carregarPedidosHojeTempoReal() {
        val inicioHoje = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val fimHoje = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        listenerPedidosHoje = db.collection("pedidos")
            .whereGreaterThanOrEqualTo("data", inicioHoje)
            .whereLessThanOrEqualTo("data", fimHoje)
            .addSnapshotListener { resultado, erro ->
                if (erro != null) {
                    tvContPedidos.text = "0"
                    return@addSnapshotListener
                }

                tvContPedidos.text = resultado?.size()?.toString() ?: "0"
            }
    }

    private fun configurarCliques() {
        btnLogout.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        navGerenEstabe.setOnClickListener {
            startActivity(Intent(this, GerenciarEstabelecimentosActivity::class.java))
        }

        navGerenUsuario.setOnClickListener {
            startActivity(Intent(this, AdminGenUsuarioActivity::class.java))
        }

        navRelatorio.setOnClickListener {
            startActivity(Intent(this, RelatoriosActivity::class.java))
        }
    }

    private fun removerListeners() {
        listenerUsuarios?.remove()
        listenerEstabelecimentos?.remove()
        listenerPedidosHoje?.remove()

        listenerUsuarios = null
        listenerEstabelecimentos = null
        listenerPedidosHoje = null
    }
}
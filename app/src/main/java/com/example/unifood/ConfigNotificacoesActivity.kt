package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConfigNotificacoesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var switchStatusPedido: Switch
    private lateinit var switchConfirmacao: Switch
    private lateinit var switchOfertas: Switch
    private lateinit var switchNovidades: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)

        switchStatusPedido = findViewById(R.id.switchStatusPedido)
        switchConfirmacao = findViewById(R.id.switchConfirmacao)
        switchOfertas = findViewById(R.id.switchOfertas)
        switchNovidades = findViewById(R.id.switchNovidades)

        carregarPreferencias()
        configurarListeners()

        findViewById<TextView>(R.id.btnVoltar).setOnClickListener { finish() }

        findViewById<LinearLayout>(R.id.navInicio).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navPedidos).setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }
    }

    // Busca as preferências salvas no Firestore e aplica nos Switches
    private fun carregarPreferencias() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                switchStatusPedido.isChecked = doc.getBoolean("notif_status_pedido") ?: true
                switchConfirmacao.isChecked = doc.getBoolean("notif_confirmacao") ?: true
                switchOfertas.isChecked = doc.getBoolean("notif_ofertas") ?: true
                switchNovidades.isChecked = doc.getBoolean("notif_novidades") ?: true
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar preferências", Toast.LENGTH_SHORT).show()
            }
    }

    // Salva no Firestore toda vez que o usuário muda um Switch
    private fun configurarListeners() {
        val uid = auth.currentUser?.uid ?: return

        switchStatusPedido.setOnCheckedChangeListener { _, isChecked ->
            db.collection("usuarios").document(uid).update("notif_status_pedido", isChecked)
        }

        switchConfirmacao.setOnCheckedChangeListener { _, isChecked ->
            db.collection("usuarios").document(uid).update("notif_confirmacao", isChecked)
        }

        switchOfertas.setOnCheckedChangeListener { _, isChecked ->
            db.collection("usuarios").document(uid).update("notif_ofertas", isChecked)
        }

        switchNovidades.setOnCheckedChangeListener { _, isChecked ->
            db.collection("usuarios").document(uid).update("notif_novidades", isChecked)
        }
    }
}
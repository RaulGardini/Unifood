package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NotificacoesActivity : AppCompatActivity() {
    private lateinit var btnVoltar: TextView
    private lateinit var btnConfig: TextView
   private lateinit var indicatorInicio: View
    private lateinit var indicatorPedidos: View
    private lateinit var indicatorPerfil: View

    private lateinit var navPerfil: LinearLayout
    private lateinit var navPedidos: LinearLayout
    private lateinit var navInicio: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificacoes)


        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val btnConfig = findViewById<TextView>(R.id.btnConfig)


        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        val indicatorInicio = findViewById<View>(R.id.indicatorInicio)
        val indicatorPedidos = findViewById<View>(R.id.indicatorPedidos)
        val indicatorPerfil = findViewById<View>(R.id.indicatorPerfil)


        indicatorPerfil.setBackgroundResource(R.color.orange)



        // VOLTAR
        btnVoltar.setOnClickListener {
            finish()
        }

        // CONFIGURAR
        btnConfig.setOnClickListener {
            //Ir nas configur
        }

        // NAV INICIO
        navInicio.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // NAV PEDIDOS
        navPedidos.setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }


        navPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}
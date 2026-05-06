package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PainelAdministrativoActivity : AppCompatActivity() {

    private lateinit var btnLogout: TextView
    private lateinit var tvContUsuario: TextView
    private lateinit var tvContEstabe: TextView

    private lateinit var navGerenEstabe: LinearLayout
    private lateinit var navGerenUsuario: LinearLayout
    private lateinit var navRelatorio: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paineladmin)

        btnLogout = findViewById(R.id.btnLogout)
        tvContUsuario = findViewById(R.id.tvContUsuario)
        tvContEstabe = findViewById(R.id.tvContEstabe)
        navGerenEstabe = findViewById(R.id.navGerenEstabe)
        navGerenUsuario = findViewById(R.id.navGerenUsuario)
        navRelatorio = findViewById(R.id.navRelatorio)

        tvContUsuario.text = "1.540"
        tvContEstabe.text = "2"

        btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        navGerenEstabe.setOnClickListener {
            val intent = Intent(this, GerenciarEstabelecimentosActivity::class.java)
            startActivity(intent)
        }

        navGerenUsuario.setOnClickListener {
            val intent = Intent(this, AdminGenUsuarioActivity::class.java)
            startActivity(intent)
        }

        navRelatorio.setOnClickListener {
            val intent = Intent(this, RelatoriosActivity::class.java)
            startActivity(intent)
        }
    }
}
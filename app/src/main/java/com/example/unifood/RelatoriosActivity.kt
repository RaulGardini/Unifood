package com.example.unifood

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RelatoriosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorios)

        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val spinnerMes = findViewById<Spinner>(R.id.spinnerMes)

        btnVoltar.setOnClickListener { finish() }

        val meses = listOf(
            "Selecionar mês",
            "Janeiro", "Fevereiro", "Março", "Abril",
            "Maio", "Junho", "Julho", "Agosto",
            "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMes.adapter = adapter
    }
}
package com.example.unifood

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class EditarPerfilLogistaActivity : AppCompatActivity() {
    private lateinit var btnVoltar: Button
    private lateinit var btnSalvar: Button
    private lateinit var edtNome: EditText
    private lateinit var edtLocal: EditText
    private lateinit var edtHorarioInicio: EditText
    private lateinit var edtHorarioFim: EditText

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

        btnSalvar.setOnClickListener {
        }

        edtHorarioInicio.setOnClickListener {
            abrirTimePicker(edtHorarioInicio)
        }

        edtHorarioFim.setOnClickListener {
            abrirTimePicker(edtHorarioFim)
        }
    }

    private fun abrirTimePicker(campo: EditText) {
        val dialog = TimePickerDialog(this, { _, hora, minuto ->
            campo.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, minuto))
        }, 8, 0, true)
        dialog.show()
    }
}

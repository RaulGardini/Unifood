package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class EditarPerfilLogistaActivity : AppCompatActivity() {
    private lateinit var btnVoltar: Button
    private lateinit var btnSalvar: Button
    private lateinit var edtNome: EditText
    private lateinit var edtLocal: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editarlogistaperfil)

        btnVoltar = findViewById(R.id.btnVoltar)
        btnSalvar = findViewById(R.id.btnSalvar)
        edtNome = findViewById(R.id.edtNome)
        edtLocal = findViewById(R.id.edtLocal)

        btnVoltar.setOnClickListener {
            finish()
        }
        btnSalvar.setOnClickListener {

        }
        edtNome.setOnClickListener {

        }

        edtLocal.setOnClickListener {

        }




    }

}
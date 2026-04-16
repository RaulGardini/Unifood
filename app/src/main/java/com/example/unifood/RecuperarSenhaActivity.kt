package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnEnviar: Button
    private lateinit var tvCadastro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        etEmail = findViewById(R.id.etEmailRec)
        btnEnviar = findViewById(R.id.btnEnviarLink)
        tvCadastro = findViewById(R.id.tvCadastroRec)

        btnEnviar.setOnClickListener {
            val email = etEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Link enviado para o email!", Toast.LENGTH_SHORT).show()
            }
        }

        tvCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

    }
}

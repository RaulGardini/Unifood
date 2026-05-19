package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnEnviar: Button
    private lateinit var tvCadastro: TextView
    private lateinit var tvErro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        etEmail = findViewById(R.id.etEmailRec)
        btnEnviar = findViewById(R.id.btnEnviarLink)
        tvCadastro = findViewById(R.id.tvCadastroRec)
        tvErro = findViewById(R.id.tvErroRec)

        btnEnviar.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                tvErro.text = getString(R.string.error_empty_email)
                tvErro.visibility = android.view.View.VISIBLE
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvErro.text = getString(R.string.error_invalid_email)
                tvErro.visibility = android.view.View.VISIBLE
            } else {
                tvErro.visibility = android.view.View.GONE
                Toast.makeText(this, "Link enviado para o email!", Toast.LENGTH_SHORT).show()
            }
        }

        tvCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

    }
}

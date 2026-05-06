
package com.example.unifood
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AlterarSenhaActivity : AppCompatActivity() {

    private lateinit var tvTelaPedidos: TextView
    private lateinit var tvTelaHome: TextView

    private lateinit var edtSenhaAtual: EditText
    private lateinit var edtNovaSenha: EditText
    private lateinit var edtConfirmarSenha: EditText


    private lateinit var btnBack: ImageButton
    private lateinit var btnAtualizarSenha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterarsenha)


        btnBack = findViewById(R.id.btnBack)
        btnAtualizarSenha = findViewById(R.id.btnAtualizarSenha)

        edtSenhaAtual = findViewById(R.id.edtSenhaAtual)
        edtNovaSenha = findViewById(R.id.edtNovaSenha)
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha)

        tvTelaHome = findViewById(R.id.tvTelaHome)
        tvTelaPedidos = findViewById(R.id.tvTelaPedidos)


        btnBack.setOnClickListener {
            finish()
        }


        btnAtualizarSenha.setOnClickListener {
            val senhaAtual = edtSenhaAtual.text.toString()
            val novaSenha = edtNovaSenha.text.toString()
            val confirmarSenha = edtConfirmarSenha.text.toString()

            if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (novaSenha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            } else {
                //  banco de dados
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }
        }


        tvTelaHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvTelaPedidos.setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package com.example.unifood

import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class ChatBotActivity : AppCompatActivity() {

    private lateinit var layoutMensagens: LinearLayout
    private lateinit var edtMensagem: EditText
    private lateinit var scrollMensagens: ScrollView

    private val scope = CoroutineScope(Dispatchers.Main)
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AQ.Ab8RN6IW4SL5e7mfgAekYStREgxny8jpgFDea6lGpcKUzzrrQA"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        layoutMensagens = findViewById(R.id.layoutMensagens)
        edtMensagem = findViewById(R.id.edtMensagem)
        scrollMensagens = findViewById(R.id.scrollMensagens)

        val btnEnviar = findViewById<ImageButton>(R.id.btnEnviar)
        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)

        carregarHistorico()

        btnVoltar.setOnClickListener { finish() }

        btnEnviar.setOnClickListener {
            val mensagem = edtMensagem.text.toString().trim()
            if (mensagem.isEmpty()) return@setOnClickListener
            adicionarMensagemUsuario(mensagem)
            salvarMensagem(mensagem, "usuario")
            edtMensagem.text.clear()
            enviarMensagemIA(mensagem)
        }
    }

    // Busca o histórico de mensagens do Firestore e exibe na tela
    private fun carregarHistorico() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid)
            .collection("chats")
            .orderBy("horario")
            .get()
            .addOnSuccessListener { resultado ->
                if (resultado.isEmpty) {
                    adicionarMensagemBot("Olá! Sou o UniBot 👋 Como posso ajudar?")
                } else {
                    resultado.documents.forEach { doc ->
                        val texto = doc.getString("texto") ?: ""
                        val tipo = doc.getString("tipo") ?: ""
                        if (tipo == "usuario") adicionarMensagemUsuario(texto)
                        else adicionarMensagemBot(texto)
                    }
                }
            }
    }

    // Salva a mensagem no Firestore dentro da subcoleção chats do usuário
    private fun salvarMensagem(texto: String, tipo: String) {
        val uid = auth.currentUser?.uid ?: return
        val mensagem = hashMapOf(
            "texto" to texto,
            "tipo" to tipo,
            "horario" to Date()
        )
        db.collection("usuarios").document(uid)
            .collection("chats")
            .add(mensagem)
    }

    private fun enviarMensagemIA(pergunta: String) {
        adicionarMensagemBot("Digitando...")

        scope.launch {
            try {
                val prompt = """
                    Você é o UniBot do restaurante UniFood.
                    Seja amigável e curto.
                    Ajude clientes com:
                    - pedidos
                    - pagamentos
                    - cardápio
                    - entrega
                    - suporte
                    Pergunta: $pergunta
                """.trimIndent()

                val resposta = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt)
                }

                removerUltimaMensagem()

                val textoResposta = resposta.text ?: "Não consegui responder."
                adicionarMensagemBot(textoResposta)
                salvarMensagem(textoResposta, "bot")

            } catch (e: Exception) {
                android.util.Log.e("ChatBot", "Erro IA: ${e.message}", e)
                removerUltimaMensagem()
                adicionarMensagemBot("Erro ao conectar com a IA.")
            }
        }
    }

    private fun adicionarMensagemUsuario(texto: String) {
        val container = LinearLayout(this)
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        container.gravity = Gravity.END

        val tv = TextView(this)
        tv.text = texto
        tv.textSize = 14f
        tv.setTextColor(resources.getColor(android.R.color.white))
        tv.setPadding(30, 20, 30, 20)
        tv.background = getDrawable(R.drawable.bg_msg_user)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(80, 10, 0, 10)
        tv.layoutParams = params

        container.addView(tv)
        layoutMensagens.addView(container)
        scrollFinal()
    }

    private fun adicionarMensagemBot(texto: String) {
        val container = LinearLayout(this)
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        container.gravity = Gravity.START

        val tv = TextView(this)
        tv.text = texto
        tv.textSize = 14f
        tv.setTextColor(resources.getColor(android.R.color.black))
        tv.setPadding(30, 20, 30, 20)
        tv.background = getDrawable(R.drawable.bg_msg_bot)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 10, 80, 10)
        tv.layoutParams = params

        container.addView(tv)
        layoutMensagens.addView(container)
        scrollFinal()
    }

    private fun removerUltimaMensagem() {
        if (layoutMensagens.childCount > 0) {
            layoutMensagens.removeViewAt(layoutMensagens.childCount - 1)
        }
    }

    private fun scrollFinal() {
        scrollMensagens.post {
            scrollMensagens.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}
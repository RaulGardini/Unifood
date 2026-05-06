package com.example.unifood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FeedbackActivity : AppCompatActivity() {

    private var selectedStars = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val btnVoltar = findViewById<TextView>(R.id.btnVoltar)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val tvRating = findViewById<TextView>(R.id.tvRating)

        val stars = listOf(
            findViewById<TextView>(R.id.star1),
            findViewById<TextView>(R.id.star2),
            findViewById<TextView>(R.id.star3),
            findViewById<TextView>(R.id.star4),
            findViewById<TextView>(R.id.star5)
        )

        val ratingLabels = listOf("Péssimo", "Ruim", "Regular", "Muito bom!", "Excelente!")

        fun updateStars(count: Int) {
            selectedStars = count
            for (i in stars.indices) {
                stars[i].setTextColor(
                    if (i < count) resources.getColor(R.color.orange, null)
                    else resources.getColor(R.color.input_border, null)
                )
            }
            tvRating.text = ratingLabels[count - 1]
        }

        for (i in stars.indices) {
            stars[i].setOnClickListener { updateStars(i + 1) }
        }

        val tags = listOf(
            findViewById<TextView>(R.id.tagInterface),
            findViewById<TextView>(R.id.tagVelocidade),
            findViewById<TextView>(R.id.tagVariedade),
            findViewById<TextView>(R.id.tagPreco),
            findViewById<TextView>(R.id.tagSuporte)
        )

        val tagSelected = BooleanArray(tags.size) { it == 0 }

        fun updateTag(index: Int) {
            tagSelected[index] = !tagSelected[index]
            if (tagSelected[index]) {
                tags[index].setBackgroundResource(R.drawable.bg_tag_selected)
                tags[index].setTextColor(resources.getColor(R.color.white, null))
            } else {
                tags[index].setBackgroundResource(R.drawable.bg_tag_unselect)
                tags[index].setTextColor(resources.getColor(R.color.text_dark, null))
            }
        }

        for (i in tags.indices) {
            tags[i].setOnClickListener { updateTag(i) }
        }

        val navInicio = findViewById<LinearLayout>(R.id.navInicio)
        val navPedidos = findViewById<LinearLayout>(R.id.navPedidos)
        val navPerfil = findViewById<LinearLayout>(R.id.navPerfil)

        btnVoltar.setOnClickListener { finish() }

        btnEnviar.setOnClickListener {
            Toast.makeText(this, "Feedback enviado! Obrigado!", Toast.LENGTH_SHORT).show()
            finish()
        }

        navInicio.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navPedidos.setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
            finish()
        }

        navPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }
    }
}

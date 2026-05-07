package com.example.unifood

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdminGenUsuarioActivity : AppCompatActivity() {

    private lateinit var statusUser: TextView
    private lateinit var btnVoltar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_geren_usuario)

        statusUser = findViewById(R.id.statusUser)
        btnVoltar = findViewById(R.id.btnVoltar)

        statusUser.setOnClickListener {

            val view = LayoutInflater.from(this)
                .inflate(R.layout.popup_usuario, null)

            val popup = PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popup.elevation = 15f
            popup.isOutsideTouchable = true
            popup.setBackgroundDrawable(ColorDrawable())

            popup.showAsDropDown(statusUser, -80, -20)
        }
        btnVoltar.setOnClickListener() {
            finish()
        }
    }
}
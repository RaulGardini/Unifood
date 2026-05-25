package com.example.unifood

data class ItemCardapio(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val preco: Double = 0.0,
    val categoria: String = "",
    val disponivel: Boolean = true
)

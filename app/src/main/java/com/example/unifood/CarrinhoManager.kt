package com.example.unifood


object CarrinhoManager {

    data class ItemCarrinho(
        val id: String,
        val nome: String,
        val preco: Double,
        var quantidade: Int = 1
    )

    private val itens = mutableListOf<ItemCarrinho>()
    var lojaId: String = ""
    var lojaNome: String = ""

    fun adicionar(item: ItemCarrinho) {
        val existente = itens.find { it.id == item.id }
        if (existente != null) {
            existente.quantidade++
        } else {
            itens.add(item)
        }
    }

    fun remover(itemId: String) {
        val existente = itens.find { it.id == itemId }
        if (existente != null) {
            if (existente.quantidade > 1) {
                existente.quantidade--
            } else {
                itens.remove(existente)
            }
        }
    }

    fun getItens(): List<ItemCarrinho> = itens.toList()

    fun getTotal(): Double = itens.sumOf { it.preco * it.quantidade }

    fun limpar() {
        itens.clear()
        lojaId = ""
        lojaNome = ""
    }

    fun isEmpty(): Boolean = itens.isEmpty()
}
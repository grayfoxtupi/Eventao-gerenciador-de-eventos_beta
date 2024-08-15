package com.example.app3

data class Event(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val hour: String = "",  // Novo campo
    val localName: String = "",  // Novo campo
    val streetNumber: String = "",  // Novo campo
    val cityState: String = "",  // Novo campo
    val imageUrl: String = ""
) {
    // Construtor sem argumentos necessário para o Firebase
    constructor() : this(0, "", "", "", "", "", "", "", "")
}


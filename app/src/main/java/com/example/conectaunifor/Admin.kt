package com.example.conectaunifor

/**
 * Modelo (molde) de um Administrador salvo no Firestore, na coleção "Admins".
 *
 * Por que todos os campos têm valor padrão ("")?
 * O Firestore, quando lê um documento e transforma de volta em objeto,
 * precisa de um "construtor sem argumentos" (uma forma de criar um Admin vazio).
 * Em Kotlin, dar um valor padrão pra cada campo cria esse construtor vazio
 * automaticamente. Sem isso, o app daria erro ao LER os admins (no login).
 */
data class Admin(
    val nome: String = "",
    val email: String = "",
    val usuario: String = "",
    val senha: String = ""
)

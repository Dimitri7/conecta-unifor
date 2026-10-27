package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Tela de entrada (launcher). Funciona como um "porteiro":
 * não mostra nada na tela, só decide pra onde mandar o usuário.
 *
 *   - Já tem sessão salva (já logou antes)? -> vai direto pra tela principal
 *     (Espaços do aluno ou do admin, conforme o tipo).
 *   - Não tem sessão? -> vai pro Login.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usuario = SessionManager.getUsuarioLogado(this)
        val destino = when (usuario?.tipo) {
            "aluno" -> AlunoEspacosActivity::class.java
            "admin" -> AdmEspacosActivity::class.java
            else -> LoginActivity::class.java
        }
        startActivity(Intent(this, destino))
        finish()
    }
}

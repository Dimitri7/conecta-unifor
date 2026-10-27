package com.example.conectaunifor

import android.content.Context

/**
 * Guarda QUEM está logado no aparelho, mesmo depois de fechar o app.
 *
 * É um "singleton" (object): existe UMA única cópia dele no app inteiro.
 * Assim qualquer tela chama SessionManager.algumaCoisa() e fala com o mesmo lugar.
 *
 * Por baixo usa o SharedPreferences — uma "gavetinha" do Android onde dá pra
 * salvar coisas pequenas (textos) que continuam lá mesmo se o app fechar.
 */
object SessionManager {

    private const val PREFS = "conecta_sessao"   // nome da gavetinha
    private const val KEY_ID = "id"
    private const val KEY_NOME = "nome"
    private const val KEY_TIPO = "tipo"          // "aluno" ou "admin"

    /** Os dados de quem está logado. */
    data class Usuario(val id: String, val nome: String, val tipo: String)

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /**
     * Salva a sessão (chamado depois de um login que deu certo).
     * tipo deve ser "aluno" ou "admin".
     */
    fun salvarSessao(context: Context, id: String, nome: String, tipo: String) {
        prefs(context).edit()
            .putString(KEY_ID, id)
            .putString(KEY_NOME, nome)
            .putString(KEY_TIPO, tipo)
            .apply()
    }

    /** Devolve quem está logado, ou null se ninguém estiver. */
    fun getUsuarioLogado(context: Context): Usuario? {
        val p = prefs(context)
        val id = p.getString(KEY_ID, null) ?: return null
        val nome = p.getString(KEY_NOME, "") ?: ""
        val tipo = p.getString(KEY_TIPO, "") ?: ""
        return Usuario(id, nome, tipo)
    }

    /** true se tem alguém logado. */
    fun estaLogado(context: Context): Boolean = getUsuarioLogado(context) != null

    /** Apaga a sessão (logout). */
    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }
}

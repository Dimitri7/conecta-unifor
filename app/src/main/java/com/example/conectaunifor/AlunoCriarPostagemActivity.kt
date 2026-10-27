package com.example.conectaunifor

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AlunoCriarPostagemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_criar_postagem)
        setupAlunoBottomNav(AlunoTab.FEED)

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etCategoria = findViewById<EditText>(R.id.etCategoria)
        val etConteudo = findViewById<EditText>(R.id.etConteudo)

        findViewById<MaterialButton>(R.id.btnPublicar).setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val categoria = etCategoria.text.toString().trim().ifBlank { "Geral" }
            val conteudo = etConteudo.text.toString().trim()
            if (titulo.isBlank() || conteudo.isBlank()) {
                showInfoDialog("Preencha todos os campos obrigatórios.")
                return@setOnClickListener
            }

            // Quem está postando vem da sessão (login).
            val usuario = SessionManager.getUsuarioLogado(this)
            if (usuario == null) {
                showInfoDialog("Sessão expirada. Faça login novamente.")
                return@setOnClickListener
            }

            val nova = Postagem(
                autorId = usuario.id,
                autorNome = usuario.nome,
                titulo = titulo,
                conteudo = conteudo,
                categoria = categoria
            )
            PostagemRepository.criarPostagem(
                nova,
                onSuccess = { showInfoDialog("Postagem publicada!") { finish() } },
                onError = { showInfoDialog("Erro ao publicar. Tente novamente.") }
            )
        }
    }
}

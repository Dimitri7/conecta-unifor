package com.example.conectaunifor

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AlunoEditarPostagemActivity : AppCompatActivity() {

    private var postagemId: String = ""
    private var autorId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_editar_postagem)
        setupAlunoBottomNav(AlunoTab.PERFIL)

        postagemId = intent.getStringExtra("postagem_id") ?: ""
        if (postagemId.isBlank()) { finish(); return }

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etCategoria = findViewById<EditText>(R.id.etCategoria)
        val etConteudo = findViewById<EditText>(R.id.etConteudo)

        // Carrega os dados atuais pra preencher os campos.
        PostagemRepository.buscarPostagem(
            postagemId,
            onSuccess = { post ->
                if (post == null) { finish(); return@buscarPostagem }
                autorId = post.autorId
                etTitulo.setText(post.titulo)
                etCategoria.setText(post.categoria)
                etConteudo.setText(post.conteudo)
            },
            onError = { showInfoDialog("Erro ao carregar a postagem.") }
        )

        // Salvar: ATUALIZA o documento (não recria), mantendo curtidas/comentários.
        findViewById<MaterialButton>(R.id.btnSalvar).setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val conteudo = etConteudo.text.toString().trim()
            if (titulo.isBlank() || conteudo.isBlank()) {
                showInfoDialog("Preencha todos os campos.")
                return@setOnClickListener
            }
            val categoria = etCategoria.text.toString().trim().ifBlank { "Geral" }
            PostagemRepository.atualizarPostagem(
                postagemId, titulo, conteudo, categoria,
                onSuccess = { showInfoDialog("Alterações salvas!") { finish() } },
                onError = { showInfoDialog("Erro ao salvar.") }
            )
        }

        // Excluir: só o autor (ou admin) consegue (checado no repositório).
        findViewById<MaterialButton>(R.id.btnExcluir).setOnClickListener {
            showConfirmDialog(
                message = getString(R.string.confirmar_excluir_post),
                actionLabel = getString(R.string.excluir),
                isDanger = true
            ) {
                PostagemRepository.excluirPostagem(
                    this, postagemId, autorId,
                    onSuccess = { finish() },
                    onError = { showInfoDialog("Não foi possível excluir.") }
                )
            }
        }
    }
}

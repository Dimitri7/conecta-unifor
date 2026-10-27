package com.example.conectaunifor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ListenerRegistration

class AlunoDetalhePostagemActivity : AppCompatActivity() {

    private var postagemId: String = ""
    private var postagemAtual: Postagem? = null
    private var registroComentarios: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_detalhe_postagem)
        setupAlunoBottomNav(AlunoTab.FEED)

        postagemId = intent.getStringExtra("postagem_id") ?: ""
        if (postagemId.isBlank()) { finish(); return }

        // 1) Carrega os dados da postagem (leitura única).
        carregarPostagem()

        // 2) Botão de enviar comentário.
        val etComentario = findViewById<EditText>(R.id.etComentario)
        findViewById<MaterialButton>(R.id.btnEnviarComentario).setOnClickListener {
            val texto = etComentario.text.toString().trim()
            if (texto.isBlank()) return@setOnClickListener
            val usuario = SessionManager.getUsuarioLogado(this)
            if (usuario == null) {
                showInfoDialog("Sessão expirada. Faça login novamente.")
                return@setOnClickListener
            }
            val c = Comentario(
                autorId = usuario.id,
                autorNome = usuario.nome,
                conteudo = texto
            )
            PostagemRepository.adicionarComentario(
                postagemId, c,
                onSuccess = { etComentario.setText("") },
                onError = { showInfoDialog("Não foi possível comentar agora.") }
            )
        }

        // 3) Botão Denunciar: SEMPRE visível pra qualquer usuário logado (inclusive o autor).
        val btnDenunciar = findViewById<MaterialButton>(R.id.btnDenunciar)
        btnDenunciar.visibility = View.VISIBLE
        btnDenunciar.setOnClickListener {
            mostrarDialogDenuncia()
        }

        // 4) Escuta os comentários em tempo real.
        registroComentarios = PostagemRepository.escutarComentarios(
            postagemId,
            onChange = { renderComentarios(it) },
            onError = { showInfoDialog("Erro ao carregar comentários.") }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        registroComentarios?.remove()
    }

    private fun carregarPostagem() {
        PostagemRepository.buscarPostagem(
            postagemId,
            onSuccess = { post ->
                if (post == null) { finish(); return@buscarPostagem }
                postagemAtual = post
                findViewById<TextView>(R.id.tvMeta).text =
                    "${post.autorNome} • ${formatarData(post.dataCriacao)} • ${post.categoria}"
                findViewById<TextView>(R.id.tvTitulo).text = post.titulo
                findViewById<TextView>(R.id.tvConteudo).text = post.conteudo
                configurarBotaoExcluir(post)
            },
            onError = { showInfoDialog("Erro ao carregar a postagem.") }
        )
    }

    /** Mostra o botão Excluir só se o usuário for o AUTOR ou um ADMIN. */
    private fun configurarBotaoExcluir(post: Postagem) {
        val btnExcluir = findViewById<MaterialButton>(R.id.btnExcluirPost)
        val usuario = SessionManager.getUsuarioLogado(this)
        val podeExcluir =
            usuario != null && (usuario.id == post.autorId || usuario.tipo == "admin")
        btnExcluir.visibility = if (podeExcluir) View.VISIBLE else View.GONE
        if (podeExcluir) {
            btnExcluir.setOnClickListener {
                showConfirmDialog(
                    message = getString(R.string.confirmar_excluir_post),
                    actionLabel = getString(R.string.excluir),
                    isDanger = true
                ) {
                    PostagemRepository.excluirPostagem(
                        this, post.id, post.autorId,
                        onSuccess = { finish() },
                        onError = { showInfoDialog("Não foi possível excluir.") }
                    )
                }
            }
        }
    }

    /** Abre uma lista de motivos e, ao confirmar, cria a denúncia (denormalizada). */
    private fun mostrarDialogDenuncia() {
        val post = postagemAtual ?: return
        val usuario = SessionManager.getUsuarioLogado(this)
        if (usuario == null) {
            showInfoDialog("Sessão expirada. Faça login novamente.")
            return
        }
        val motivos = arrayOf(
            "Conteúdo Ofensivo", "Spam", "Discurso de Ódio", "Informação Falsa", "Outro"
        )
        var escolhido = 0
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.escolher_motivo_denuncia))
            .setSingleChoiceItems(motivos, 0) { _, which -> escolhido = which }
            .setNegativeButton(getString(R.string.cancelar)) { d, _ -> d.dismiss() }
            .setPositiveButton(getString(R.string.enviar)) { d, _ ->
                d.dismiss()
                val denuncia = Denuncia(
                    postagemId = post.id,
                    postagemTitulo = post.titulo,
                    postagemConteudo = post.conteudo,
                    postagemAutorNome = post.autorNome,
                    motivo = motivos[escolhido],
                    denuncianteId = usuario.id,
                    denuncianteNome = usuario.nome
                )
                PostagemRepository.criarDenuncia(
                    denuncia,
                    onSuccess = {
                        Toast.makeText(
                            this, getString(R.string.denuncia_enviada), Toast.LENGTH_LONG
                        ).show()
                    },
                    onError = { showInfoDialog("Não foi possível enviar a denúncia agora.") }
                )
            }
            .show()
    }

    private fun renderComentarios(comentarios: List<Comentario>) {
        val container = findViewById<LinearLayout>(R.id.listaComentarios)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        for (c in comentarios) {
            val view = inflater.inflate(R.layout.item_comentario, container, false)
            view.findViewById<TextView>(R.id.tvComentario).text = "${c.autorNome}: ${c.conteudo}"
            container.addView(view)
        }
        if (comentarios.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Nenhum comentário ainda. Seja o primeiro!"
                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        this@AlunoDetalhePostagemActivity, R.color.colorTextMuted
                    )
                )
                textSize = 13f
                setPadding(8, 8, 8, 8)
            }
            container.addView(tv)
        }
    }
}

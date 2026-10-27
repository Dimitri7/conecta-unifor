package com.example.conectaunifor

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

enum class PerfilAba { POSTS, SALVOS, DESEJADOS, ALUGADOS }

class AlunoPerfilActivity : AppCompatActivity() {

    private var abaAtual: PerfilAba = PerfilAba.POSTS
    private lateinit var tabs: List<Pair<PerfilAba, TextView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_perfil)
        setupAlunoBottomNav(AlunoTab.PERFIL)

        val tabPosts = findViewById<TextView>(R.id.tabPosts)
        val tabSalvos = findViewById<TextView>(R.id.tabSalvos)
        val tabDesejados = findViewById<TextView>(R.id.tabDesejados)
        val tabAlugados = findViewById<TextView>(R.id.tabAlugados)
        tabs = listOf(
            PerfilAba.POSTS to tabPosts,
            PerfilAba.SALVOS to tabSalvos,
            PerfilAba.DESEJADOS to tabDesejados,
            PerfilAba.ALUGADOS to tabAlugados
        )
        tabs.forEach { (aba, tv) ->
            tv.setOnClickListener {
                if (abaAtual != aba) {
                    abaAtual = aba
                    aplicarToggle()
                    renderConteudo()
                }
            }
        }

        // Mostra o nome de quem está logado no cabeçalho.
        findViewById<TextView>(R.id.tvNome).text =
            SessionManager.getUsuarioLogado(this)?.nome ?: getString(R.string.aluno)

        // Botão "Sair" (logout): confirma, apaga a sessão e volta pro Login.
        findViewById<TextView>(R.id.tvLogout).setOnClickListener {
            showConfirmDialog(
                message = getString(R.string.confirmar_logout),
                actionLabel = getString(R.string.sair),
                isDanger = true
            ) {
                SessionManager.logout(this)
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            }
        }

        aplicarToggle()
    }

    override fun onResume() {
        super.onResume()
        renderConteudo()
    }

    private fun aplicarToggle() {
        for ((aba, tv) in tabs) {
            val selected = aba == abaAtual
            tv.setBackgroundResource(
                if (selected) R.drawable.bg_toggle_selected else R.drawable.bg_toggle_unselected
            )
            tv.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (selected) R.color.colorPrimary else R.color.colorTextMuted
                )
            )
            tv.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun renderConteudo() {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        when (abaAtual) {
            PerfilAba.POSTS -> {
                // POSTS agora vem do Firestore: os posts criados por mim.
                val usuarioId = SessionManager.getUsuarioLogado(this)?.id ?: ""
                PostagemRepository.buscarPostagensDoAutor(
                    usuarioId,
                    onSuccess = { lista ->
                        container.removeAllViews()
                        renderPostagens(container, inflater, lista, editavel = true)
                        if (container.childCount == 0) mostrarVazio(container)
                    },
                    onError = {
                        container.removeAllViews()
                        mostrarVazio(container)
                    }
                )
                return // o placeholder de vazio é tratado dentro do callback
            }
            // SALVOS não foi migrado nesta fase (lista vazia -> mostra "Nada por aqui").
            PerfilAba.SALVOS -> renderPostagens(container, inflater, MockRepository.postagensSalvas, editavel = false)
            PerfilAba.DESEJADOS -> renderLivros(container, inflater, MockRepository.livrosDesejados)
            PerfilAba.ALUGADOS -> renderAlugueis(container, inflater, MockRepository.livrosAlugados)
        }

        if (container.childCount == 0) mostrarVazio(container)
    }

    private fun mostrarVazio(container: LinearLayout) {
        val tv = TextView(this).apply {
            text = "Nada por aqui ainda."
            setTextColor(ContextCompat.getColor(this@AlunoPerfilActivity, R.color.colorTextMuted))
            textSize = 14f
            setPadding(8, 24, 8, 8)
        }
        container.addView(tv)
    }

    private fun renderPostagens(
        container: LinearLayout,
        inflater: LayoutInflater,
        lista: List<Postagem>,
        editavel: Boolean
    ) {
        for (post in lista) {
            val view = inflater.inflate(R.layout.item_postagem, container, false) as MaterialCardView
            view.findViewById<TextView>(R.id.tvMeta).text =
                "${post.autorNome} • ${formatarData(post.dataCriacao)} • ${post.categoria}"
            view.findViewById<TextView>(R.id.tvTitulo).text = post.titulo
            view.findViewById<TextView>(R.id.tvConteudo).text = post.conteudo
            view.findViewById<TextView>(R.id.tvCurtidas).text = "👍  ${post.curtidasIds.size} Curtidas"
            view.findViewById<TextView>(R.id.tvComentarios).text =
                "💬  ${post.qtdComentarios} Comentários"
            view.setOnClickListener {
                val target = if (editavel) AlunoEditarPostagemActivity::class.java
                             else AlunoDetalhePostagemActivity::class.java
                val it2 = Intent(this, target)
                it2.putExtra("postagem_id", post.id)
                startActivity(it2)
            }
            container.addView(view)
        }
    }

    private fun renderLivros(
        container: LinearLayout,
        inflater: LayoutInflater,
        lista: List<Livro>
    ) {
        for (livro in lista) {
            val view = inflater.inflate(R.layout.item_livro, container, false) as MaterialCardView
            view.findViewById<TextView>(R.id.tvTitulo).text = livro.titulo
            val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
            tvStatus.text = livro.status.label()
            tvStatus.setTextColor(ContextCompat.getColor(this, livro.status.colorResId()))
            view.findViewById<TextView>(R.id.tvAutor).text = "Autor: ${livro.autor}"
            view.findViewById<TextView>(R.id.tvGenero).text = "Gênero: ${livro.genero}"
            view.setOnClickListener {
                val it2 = Intent(this, AlunoDetalheLivroActivity::class.java)
                it2.putExtra("livro_id", livro.id)
                startActivity(it2)
            }
            container.addView(view)
        }
    }

    private fun renderAlugueis(
        container: LinearLayout,
        inflater: LayoutInflater,
        lista: List<Aluguel>
    ) {
        for (aluguel in lista) {
            val view = inflater.inflate(R.layout.item_aluguel_perfil, container, false)
            view.findViewById<TextView>(R.id.tvAluguel).text =
                "Livro: ${aluguel.livroTitulo}  •  Prazo: ${aluguel.prazo}"
            container.addView(view)
        }
    }
}

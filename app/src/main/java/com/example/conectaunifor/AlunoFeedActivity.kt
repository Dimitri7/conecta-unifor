package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.ListenerRegistration

class AlunoFeedActivity : AppCompatActivity() {

    // "Controle remoto" da escuta em tempo real — guardamos pra desligar depois.
    private var registro: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_feed)
        setupAlunoBottomNav(AlunoTab.FEED)

        findViewById<MaterialButton>(R.id.btnCriarPostagem).setOnClickListener {
            startActivity(Intent(this, AlunoCriarPostagemActivity::class.java))
        }

        // Liga a escuta: sempre que o banco mudar, a lista se atualiza sozinha.
        registro = PostagemRepository.escutarPostagens(
            onChange = { lista -> mostrarPostagens(lista) },
            onError = { showInfoDialog("Erro ao carregar o feed.") }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        registro?.remove() // desliga a escuta pra não desperdiçar recursos
    }

    private fun mostrarPostagens(lista: List<Postagem>) {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val usuarioId = SessionManager.getUsuarioLogado(this)?.id ?: ""

        for (post in lista) {
            val view = inflater.inflate(R.layout.item_postagem, container, false) as MaterialCardView
            view.findViewById<TextView>(R.id.tvMeta).text =
                "${post.autorNome} • ${formatarData(post.dataCriacao)} • ${post.categoria}"
            view.findViewById<TextView>(R.id.tvTitulo).text = post.titulo
            view.findViewById<TextView>(R.id.tvConteudo).text = post.conteudo

            val tvCurtidas = view.findViewById<TextView>(R.id.tvCurtidas)
            tvCurtidas.text = "👍  ${post.curtidasIds.size} Curtidas"
            // Tocar no nº de curtidas curte/descurte (sem abrir o post).
            tvCurtidas.setOnClickListener {
                if (usuarioId.isBlank()) return@setOnClickListener
                PostagemRepository.alternarCurtida(
                    post.id, usuarioId,
                    onSuccess = { /* a escuta atualiza a tela sozinha */ },
                    onError = { showInfoDialog("Não foi possível curtir agora.") }
                )
            }

            view.findViewById<TextView>(R.id.tvComentarios).text =
                "💬  ${post.qtdComentarios} Comentários"

            view.setOnClickListener {
                val it2 = Intent(this, AlunoDetalhePostagemActivity::class.java)
                it2.putExtra("postagem_id", post.id)
                startActivity(it2)
            }
            container.addView(view)
        }
    }
}

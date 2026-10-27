package com.example.conectaunifor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class AlunoDetalheLivroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_detalhe_livro)
        setupAlunoBottomNav(AlunoTab.ALUGUEL)

        val id = intent.getIntExtra("livro_id", -1)
        val livro = MockRepository.livros.firstOrNull { it.id == id }
        if (livro == null) { finish(); return }

        findViewById<TextView>(R.id.tvHeaderTitle).text = livro.titulo
        findViewById<TextView>(R.id.tvTitulo).text = livro.titulo
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = "Status: ${livro.status.label()}"
        tvStatus.setTextColor(ContextCompat.getColor(this, livro.status.colorResId()))
        findViewById<TextView>(R.id.tvAutor).text = "Autor: ${livro.autor}"
        findViewById<TextView>(R.id.tvGenero).text = "Gênero: ${livro.genero}"
        findViewById<TextView>(R.id.tvDescricao).text = livro.descricao

        val btn = findViewById<MaterialButton>(R.id.btnSolicitarAluguel)
        if (livro.status != StatusEspaco.LIVRE) {
            btn.text = "INDISPONÍVEL"
            btn.isEnabled = false
            btn.alpha = 0.6f
        } else {
            btn.setOnClickListener {
                showConfirmDialog(
                    message = "Solicitar aluguel de \"${livro.titulo}\"?",
                    actionLabel = getString(R.string.confirmar),
                    isDanger = false
                ) {
                    MockRepository.solicitacoesAluguel.add(
                        Aluguel(
                            id = (MockRepository.solicitacoesAluguel.maxOfOrNull { it.id } ?: 0) + 1,
                            livroTitulo = livro.titulo,
                            alunoNome = "Aluno",
                            prazo = "—"
                        )
                    )
                    livro.status = StatusEspaco.INDISPONIVEL
                    showInfoDialog(getString(R.string.aluguel_solicitado)) { finish() }
                }
            }
        }
    }
}

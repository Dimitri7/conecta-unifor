package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class AlunoAluguelActivity : AppCompatActivity() {

    private var apenasDisponiveis: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_aluguel)
        setupAlunoBottomNav(AlunoTab.ALUGUEL)

        findViewById<TextView>(R.id.btnFiltros).setOnClickListener { abrirFiltros() }
        recarregarLista()
    }

    override fun onResume() {
        super.onResume()
        recarregarLista()
    }

    private fun recarregarLista() {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val items = MockRepository.livros.filter {
            !apenasDisponiveis || it.status == StatusEspaco.LIVRE
        }
        for (livro in items) {
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

    private fun abrirFiltros() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_filtros_livros, null)
        val chk = view.findViewById<CheckBox>(R.id.chkApenasDisponiveis)
        chk.isChecked = apenasDisponiveis
        AlertDialog.Builder(this)
            .setView(view)
            .setNegativeButton(R.string.cancelar) { d, _ -> d.dismiss() }
            .setPositiveButton(R.string.aplicar) { d, _ ->
                apenasDisponiveis = chk.isChecked
                recarregarLista()
                d.dismiss()
            }
            .show()
    }
}

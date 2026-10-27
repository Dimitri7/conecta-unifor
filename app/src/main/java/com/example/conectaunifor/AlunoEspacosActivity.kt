package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class AlunoEspacosActivity : AppCompatActivity() {

    private var tipoAtual: TipoEspaco = TipoEspaco.CAPSULA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_espacos)
        setupAlunoBottomNav(AlunoTab.ESPACOS)

        val tabCapsulas = findViewById<TextView>(R.id.tabCapsulas)
        val tabSalas = findViewById<TextView>(R.id.tabSalas)

        tabCapsulas.setOnClickListener {
            if (tipoAtual != TipoEspaco.CAPSULA) {
                tipoAtual = TipoEspaco.CAPSULA
                aplicarToggle(tabCapsulas, tabSalas)
                recarregarLista()
            }
        }
        tabSalas.setOnClickListener {
            if (tipoAtual != TipoEspaco.SALA) {
                tipoAtual = TipoEspaco.SALA
                aplicarToggle(tabSalas, tabCapsulas)
                recarregarLista()
            }
        }

        recarregarLista()
    }

    override fun onResume() {
        super.onResume()
        recarregarLista()
    }

    private fun aplicarToggle(selecionado: TextView, naoSelecionado: TextView) {
        selecionado.setBackgroundResource(R.drawable.bg_toggle_selected)
        selecionado.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        selecionado.setTypeface(null, android.graphics.Typeface.BOLD)
        naoSelecionado.setBackgroundResource(R.drawable.bg_toggle_unselected)
        naoSelecionado.setTextColor(ContextCompat.getColor(this, R.color.colorTextMuted))
        naoSelecionado.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun recarregarLista() {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val items = MockRepository.espacos.filter { it.tipo == tipoAtual }
        for (espaco in items) {
            val itemView = inflater.inflate(R.layout.item_espaco, container, false) as MaterialCardView
            itemView.findViewById<TextView>(R.id.tvNome).text = espaco.nome
            val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
            tvStatus.text = espaco.status.label()
            tvStatus.setTextColor(ContextCompat.getColor(this, espaco.status.colorResId()))
            itemView.findViewById<TextView>(R.id.tvLocal).text = "Local: ${espaco.local}"
            itemView.setOnClickListener {
                val it2 = Intent(this, AlunoDetalheCapsulaActivity::class.java)
                it2.putExtra("espaco_id", espaco.id)
                startActivity(it2)
            }
            container.addView(itemView)
        }
    }
}

package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AdmEspacosActivity : AppCompatActivity() {

    private var tipoAtual: TipoEspaco = TipoEspaco.CAPSULA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_espacos)
        setupAdminBottomNav(AdminTab.ESPACOS)

        val tabCapsulas = findViewById<TextView>(R.id.tabCapsulas)
        val tabSalas = findViewById<TextView>(R.id.tabSalas)
        val btnNovo = findViewById<MaterialButton>(R.id.btnNovo)

        tabCapsulas.setOnClickListener {
            if (tipoAtual != TipoEspaco.CAPSULA) {
                tipoAtual = TipoEspaco.CAPSULA
                btnNovo.text = getString(R.string.nova_capsula_btn)
                aplicarToggle(tabCapsulas, tabSalas); recarregar()
            }
        }
        tabSalas.setOnClickListener {
            if (tipoAtual != TipoEspaco.SALA) {
                tipoAtual = TipoEspaco.SALA
                btnNovo.text = getString(R.string.nova_sala_btn)
                aplicarToggle(tabSalas, tabCapsulas); recarregar()
            }
        }

        btnNovo.setOnClickListener {
            val it2 = Intent(this, AdmFormEspacoActivity::class.java)
            it2.putExtra("tipo", tipoAtual.name)
            startActivity(it2)
        }

        findViewById<TextView>(R.id.btnVerAcervo).setOnClickListener {
            startActivity(Intent(this, AdmAcervoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        recarregar()
    }

    private fun aplicarToggle(selecionado: TextView, naoSelecionado: TextView) {
        selecionado.setBackgroundResource(R.drawable.bg_toggle_selected)
        selecionado.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        selecionado.setTypeface(null, android.graphics.Typeface.BOLD)
        naoSelecionado.setBackgroundResource(R.drawable.bg_toggle_unselected)
        naoSelecionado.setTextColor(ContextCompat.getColor(this, R.color.colorTextMuted))
        naoSelecionado.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun recarregar() {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val items = MockRepository.espacos.filter { it.tipo == tipoAtual }
        for (espaco in items) {
            val view = inflater.inflate(R.layout.item_espaco, container, false) as MaterialCardView
            view.findViewById<TextView>(R.id.tvNome).text = espaco.nome
            val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
            tvStatus.text = espaco.status.label()
            tvStatus.setTextColor(ContextCompat.getColor(this, espaco.status.colorResId()))
            view.findViewById<TextView>(R.id.tvLocal).text = "Local: ${espaco.local}"
            view.setOnClickListener {
                val it2 = Intent(this, AdmFormEspacoActivity::class.java)
                it2.putExtra("espaco_id", espaco.id)
                it2.putExtra("tipo", espaco.tipo.name)
                startActivity(it2)
            }
            container.addView(view)
        }
    }
}

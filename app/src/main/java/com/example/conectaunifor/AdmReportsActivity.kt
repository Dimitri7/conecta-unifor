package com.example.conectaunifor

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class AdmReportsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_reports)
        setupAdminBottomNav(AdminTab.REPORTS)

        findViewById<TextView>(R.id.btnVerAlugueis).setOnClickListener {
            startActivity(android.content.Intent(this, AdmAlugueisActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        recarregar()
    }

    private fun recarregar() {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        if (MockRepository.reportes.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Nenhum problema reportado."
                setTextColor(ContextCompat.getColor(this@AdmReportsActivity, R.color.colorTextMuted))
                textSize = 14f
                setPadding(8, 24, 8, 8)
            }
            container.addView(tv); return
        }
        for (rep in MockRepository.reportes) {
            val view = inflater.inflate(R.layout.item_reporte, container, false)
            view.findViewById<TextView>(R.id.tvMeta).text =
                "Local: ${rep.espacoNome}  •  Relatado por: ${rep.relator}"
            view.findViewById<TextView>(R.id.tvDescricao).text = rep.descricao
            view.findViewById<MaterialButton>(R.id.btnResolver).setOnClickListener {
                MockRepository.reportes.removeAll { it.id == rep.id }
                showInfoDialog("Problema marcado como resolvido.") { recarregar() }
            }
            view.findViewById<MaterialButton>(R.id.btnInterditar).setOnClickListener {
                showConfirmDialog(
                    message = getString(R.string.confirmar_interditar),
                    actionLabel = getString(R.string.interditar),
                    isDanger = true
                ) {
                    MockRepository.espacos.firstOrNull { it.nome == rep.espacoNome }?.let {
                        it.status = StatusEspaco.INDISPONIVEL
                    }
                    MockRepository.reportes.removeAll { it.id == rep.id }
                    recarregar()
                }
            }
            container.addView(view)
        }
    }
}

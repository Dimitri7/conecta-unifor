package com.example.conectaunifor

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class AdmAlugueisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_alugueis)
        setupAdminBottomNav(AdminTab.REPORTS)
    }

    override fun onResume() {
        super.onResume()
        recarregar()
    }

    private fun recarregar() {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        if (MockRepository.solicitacoesAluguel.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Nenhuma solicitação pendente."
                setTextColor(ContextCompat.getColor(this@AdmAlugueisActivity, R.color.colorTextMuted))
                textSize = 14f
                setPadding(8, 24, 8, 8)
            }
            container.addView(tv); return
        }
        for (a in MockRepository.solicitacoesAluguel) {
            val view = inflater.inflate(R.layout.item_aluguel_admin, container, false)
            view.findViewById<TextView>(R.id.tvDescricao).text =
                "O aluno ${a.alunoNome} solicitou o livro \"${a.livroTitulo}\""
            view.findViewById<MaterialButton>(R.id.btnAprovar).setOnClickListener {
                showConfirmDialog(
                    message = "Aprovar a retirada deste livro?",
                    actionLabel = "Aprovar",
                    isDanger = false
                ) {
                    MockRepository.solicitacoesAluguel.removeAll { it.id == a.id }
                    MockRepository.livrosAlugados.add(a)
                    recarregar()
                }
            }
            container.addView(view)
        }
    }
}

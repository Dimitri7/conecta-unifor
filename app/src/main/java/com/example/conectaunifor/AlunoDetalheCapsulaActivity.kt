package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class AlunoDetalheCapsulaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_detalhe_capsula)
        setupAlunoBottomNav(AlunoTab.ESPACOS)

        val id = intent.getIntExtra("espaco_id", -1)
        val espaco = MockRepository.espacos.firstOrNull { it.id == id }
        if (espaco == null) { finish(); return }

        findViewById<TextView>(R.id.tvHeaderTitle).text = espaco.nome
        findViewById<TextView>(R.id.tvNome).text = espaco.nome
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = "Status: ${espaco.status.label()}"
        tvStatus.setTextColor(ContextCompat.getColor(this, espaco.status.colorResId()))
        findViewById<TextView>(R.id.tvLocal).text = "Local: ${espaco.local}"
        findViewById<TextView>(R.id.tvDescricao).text = espaco.descricao

        findViewById<MaterialButton>(R.id.btnReportarProblema).setOnClickListener {
            val it2 = Intent(this, AlunoRelatarProblemaActivity::class.java)
            it2.putExtra("espaco_id", espaco.id)
            startActivity(it2)
        }
    }
}

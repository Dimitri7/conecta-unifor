package com.example.conectaunifor

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AlunoRelatarProblemaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_relatar_problema)
        setupAlunoBottomNav(AlunoTab.ESPACOS)

        val id = intent.getIntExtra("espaco_id", -1)
        val espaco = MockRepository.espacos.firstOrNull { it.id == id }
        val nome = espaco?.nome ?: "Espaço"
        findViewById<TextView>(R.id.tvHeaderSubtitle).text = nome

        val etRelato = findViewById<EditText>(R.id.etRelato)
        findViewById<MaterialButton>(R.id.btnEnviar).setOnClickListener {
            val descricao = etRelato.text.toString().trim()
            if (descricao.isBlank()) {
                showInfoDialog("Descreva o problema antes de enviar.")
                return@setOnClickListener
            }
            MockRepository.reportes.add(
                Reporte(
                    id = (MockRepository.reportes.maxOfOrNull { it.id } ?: 0) + 1,
                    espacoNome = nome,
                    descricao = descricao,
                    relator = "Aluno"
                )
            )
            showInfoDialog(getString(R.string.problema_registrado)) { finish() }
        }
    }
}

package com.example.conectaunifor

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdmFormEspacoActivity : AppCompatActivity() {

    private var editandoId: Int = -1
    private lateinit var tipo: TipoEspaco

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_form_espaco)
        setupAdminBottomNav(AdminTab.ESPACOS)

        editandoId = intent.getIntExtra("espaco_id", -1)
        tipo = TipoEspaco.valueOf(
            intent.getStringExtra("tipo") ?: TipoEspaco.CAPSULA.name
        )

        val tvHeader = findViewById<TextView>(R.id.tvHeaderTitle)
        val etNome = findViewById<EditText>(R.id.etNome)
        val etLocal = findViewById<EditText>(R.id.etLocal)
        val etCapacidade = findViewById<EditText>(R.id.etCapacidade)
        val btnSalvar = findViewById<MaterialButton>(R.id.btnSalvar)
        val btnExcluir = findViewById<MaterialButton>(R.id.btnExcluir)

        val tipoLabel = if (tipo == TipoEspaco.CAPSULA) "Cápsula" else "Sala"

        if (editandoId == -1) {
            tvHeader.text = "Cadastrar Nova $tipoLabel"
            btnSalvar.text = getString(R.string.cadastrar_simples)
            btnExcluir.visibility = android.view.View.GONE
        } else {
            tvHeader.text = "Editar $tipoLabel"
            btnSalvar.text = getString(R.string.salvar_alteracoes)
            btnExcluir.visibility = android.view.View.VISIBLE
            btnExcluir.text = if (tipo == TipoEspaco.CAPSULA)
                getString(R.string.excluir_capsula) else getString(R.string.excluir_sala)

            val espaco = MockRepository.espacos.firstOrNull { it.id == editandoId }
            if (espaco == null) { finish(); return }
            etNome.setText(espaco.nome)
            etLocal.setText(espaco.local)
            etCapacidade.setText(espaco.capacidade.toString())
        }

        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val local = etLocal.text.toString().trim()
            val cap = etCapacidade.text.toString().toIntOrNull() ?: 0
            if (nome.isBlank() || local.isBlank()) {
                showInfoDialog("Preencha nome e localização."); return@setOnClickListener
            }
            if (editandoId == -1) {
                MockRepository.espacos.add(
                    Espaco(
                        id = MockRepository.proximoIdEspaco(),
                        nome = nome, status = StatusEspaco.LIVRE,
                        local = local, capacidade = cap,
                        descricao = "", tipo = tipo
                    )
                )
                showInfoDialog("$tipoLabel cadastrada!") { finish() }
            } else {
                val espaco = MockRepository.espacos.first { it.id == editandoId }
                espaco.nome = nome
                espaco.local = local
                espaco.capacidade = cap
                showInfoDialog("Alterações salvas!") { finish() }
            }
        }

        btnExcluir.setOnClickListener {
            val msg = if (tipo == TipoEspaco.CAPSULA)
                getString(R.string.confirmar_excluir_capsula)
            else getString(R.string.confirmar_excluir_sala)
            showConfirmDialog(message = msg, actionLabel = getString(R.string.excluir)) {
                MockRepository.espacos.removeAll { it.id == editandoId }
                finish()
            }
        }
    }
}

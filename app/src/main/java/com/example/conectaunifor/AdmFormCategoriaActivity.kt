package com.example.conectaunifor

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdmFormCategoriaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_form_categoria)
        setupAdminBottomNav(AdminTab.MODERACAO)

        val index = intent.getIntExtra("categoria_index", -1)
        val tvHeader = findViewById<TextView>(R.id.tvHeaderTitle)
        val etNome = findViewById<EditText>(R.id.etNome)
        val btnSalvar = findViewById<MaterialButton>(R.id.btnSalvar)
        val btnExcluir = findViewById<MaterialButton>(R.id.btnExcluir)

        if (index == -1) {
            tvHeader.text = "Nova Categoria"
            btnSalvar.text = getString(R.string.cadastrar_simples)
            btnExcluir.visibility = android.view.View.GONE
        } else {
            tvHeader.text = "Editar Categoria"
            btnSalvar.text = getString(R.string.salvar_alteracoes)
            btnExcluir.visibility = android.view.View.VISIBLE
            etNome.setText(MockRepository.categorias.getOrNull(index) ?: "")
        }

        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            if (nome.isBlank()) { showInfoDialog("Informe o nome."); return@setOnClickListener }
            if (index == -1) {
                MockRepository.categorias.add(nome)
            } else {
                MockRepository.categorias[index] = nome
            }
            showInfoDialog("Categoria salva!") { finish() }
        }

        btnExcluir.setOnClickListener {
            showConfirmDialog(
                message = getString(R.string.confirmar_excluir_categoria),
                actionLabel = getString(R.string.excluir),
                isDanger = true
            ) {
                if (index in MockRepository.categorias.indices) {
                    MockRepository.categorias.removeAt(index)
                }
                finish()
            }
        }
    }
}

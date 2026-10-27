package com.example.conectaunifor

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdmFormLivroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_form_livro)
        setupAdminBottomNav(AdminTab.ESPACOS)

        val id = intent.getIntExtra("livro_id", -1)
        val tvHeader = findViewById<TextView>(R.id.tvHeaderTitle)
        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etAutor = findViewById<EditText>(R.id.etAutor)
        val etGenero = findViewById<EditText>(R.id.etGenero)
        val btnSalvar = findViewById<MaterialButton>(R.id.btnSalvar)
        val btnExcluir = findViewById<MaterialButton>(R.id.btnExcluir)

        if (id == -1) {
            tvHeader.text = "Cadastrar Livro"
            btnSalvar.text = getString(R.string.cadastrar_simples)
            btnExcluir.visibility = android.view.View.GONE
        } else {
            tvHeader.text = "Editar Livro"
            btnSalvar.text = getString(R.string.salvar_alteracoes)
            btnExcluir.visibility = android.view.View.VISIBLE
            val livro = MockRepository.livros.firstOrNull { it.id == id }
            if (livro == null) { finish(); return }
            etTitulo.setText(livro.titulo)
            etAutor.setText(livro.autor)
            etGenero.setText(livro.genero)
        }

        btnSalvar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val autor = etAutor.text.toString().trim()
            val genero = etGenero.text.toString().trim()
            if (titulo.isBlank() || autor.isBlank() || genero.isBlank()) {
                showInfoDialog("Preencha título, autor e gênero."); return@setOnClickListener
            }
            if (id == -1) {
                MockRepository.livros.add(
                    Livro(MockRepository.proximoIdLivro(), titulo, autor, genero, StatusEspaco.LIVRE, "")
                )
                showInfoDialog("Livro cadastrado!") { finish() }
            } else {
                val l = MockRepository.livros.first { it.id == id }
                l.titulo = titulo; l.autor = autor; l.genero = genero
                showInfoDialog("Alterações salvas!") { finish() }
            }
        }

        btnExcluir.setOnClickListener {
            showConfirmDialog(
                message = "Tem certeza que deseja excluir este livro?",
                actionLabel = getString(R.string.excluir),
                isDanger = true
            ) {
                MockRepository.livros.removeAll { it.id == id }
                finish()
            }
        }
    }
}

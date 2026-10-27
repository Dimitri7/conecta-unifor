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

class AdmAcervoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_acervo)
        setupAdminBottomNav(AdminTab.ESPACOS)

        findViewById<MaterialButton>(R.id.btnNovo).setOnClickListener {
            startActivity(Intent(this, AdmFormLivroActivity::class.java))
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
        for (livro in MockRepository.livros) {
            val view = inflater.inflate(R.layout.item_livro, container, false) as MaterialCardView
            view.findViewById<TextView>(R.id.tvTitulo).text = livro.titulo
            val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
            tvStatus.text = livro.status.label()
            tvStatus.setTextColor(ContextCompat.getColor(this, livro.status.colorResId()))
            view.findViewById<TextView>(R.id.tvAutor).text = "Autor: ${livro.autor}"
            view.findViewById<TextView>(R.id.tvGenero).text = "Gênero: ${livro.genero}"
            view.setOnClickListener {
                val it2 = Intent(this, AdmFormLivroActivity::class.java)
                it2.putExtra("livro_id", livro.id)
                startActivity(it2)
            }
            container.addView(view)
        }
    }
}

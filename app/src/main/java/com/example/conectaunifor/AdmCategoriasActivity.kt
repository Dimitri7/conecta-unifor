package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdmCategoriasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_categorias)
        setupAdminBottomNav(AdminTab.MODERACAO)

        findViewById<MaterialButton>(R.id.btnNova).setOnClickListener {
            startActivity(Intent(this, AdmFormCategoriaActivity::class.java))
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
        for ((i, nome) in MockRepository.categorias.withIndex()) {
            val view = inflater.inflate(R.layout.item_categoria, container, false)
            view.findViewById<TextView>(R.id.tvNome).text = nome
            view.setOnClickListener {
                val it2 = Intent(this, AdmFormCategoriaActivity::class.java)
                it2.putExtra("categoria_index", i)
                startActivity(it2)
            }
            container.addView(view)
        }
    }
}

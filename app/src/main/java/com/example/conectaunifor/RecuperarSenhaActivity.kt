package com.example.conectaunifor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RecuperarSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val btnEnviar = findViewById<MaterialButton>(R.id.btnEnviar)
        val tvVoltarLogin = findViewById<TextView>(R.id.tvVoltarLogin)

        btnEnviar.setOnClickListener {
            if (etEmail.text.isNullOrBlank()) {
                showInfoDialog("Informe seu e-mail ou matrícula.")
                return@setOnClickListener
            }
            showInfoDialog(getString(R.string.instrucoes_enviadas)) { finish() }
        }
        tvVoltarLogin.setOnClickListener { finish() }
    }
}

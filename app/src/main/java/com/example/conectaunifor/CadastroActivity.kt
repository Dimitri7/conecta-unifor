package com.example.conectaunifor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val etNome = findViewById<TextInputEditText>(R.id.etNome)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etMatricula = findViewById<TextInputEditText>(R.id.etMatricula)
        val etUsuario = findViewById<TextInputEditText>(R.id.etUsuario)
        val etSenha = findViewById<TextInputEditText>(R.id.etSenha)

        val btnCadastrar = findViewById<MaterialButton>(R.id.btnCadastrar)
        val tvFazerLogin = findViewById<TextView>(R.id.tvFazerLogin)

        val fb = Firebase.firestore

        btnCadastrar.setOnClickListener {

            val campos = listOf(etNome, etEmail, etMatricula, etUsuario, etSenha)

            val faltando = campos.any { it.text.isNullOrBlank() }

            if (faltando) {
                showInfoDialog("Preencha todos os campos obrigatórios.")
                return@setOnClickListener
            }

            val senha = etSenha.text.toString()

            if (senha.length < 8) {
                showInfoDialog("A senha deve ter no mínimo 8 caracteres.")
                return@setOnClickListener
            }

            val aluno = hashMapOf(
                "nome" to etNome.text.toString(),
                "email" to etEmail.text.toString(),
                "matricula" to etMatricula.text.toString(),
                "usuario" to etUsuario.text.toString(),
                "senha" to senha
            )

            fb.collection("Alunos")
                .add(aluno)
                .addOnSuccessListener {
                    showInfoDialog(getString(R.string.cadastro_sucesso)) {
                        finish()
                    }
                }
                .addOnFailureListener {
                    showInfoDialog("Erro ao cadastrar.")
                }
        }

        tvFazerLogin.setOnClickListener {
            finish()
        }
    }
}
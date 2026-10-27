package com.example.conectaunifor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Tela de cadastro de ADMINISTRADOR.
 *
 * Foi feita espelhando a CadastroActivity (cadastro de aluno do João),
 * com duas diferenças combinadas com o Dimitri:
 *  1) o admin NÃO tem matrícula (só nome, email, usuário e senha);
 *  2) salva na coleção "Admins" (e não em "Alunos").
 */
class CadastroAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_admin)

        // Pega os campos da tela
        val etNome = findViewById<TextInputEditText>(R.id.etNome)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etUsuario = findViewById<TextInputEditText>(R.id.etUsuario)
        val etSenha = findViewById<TextInputEditText>(R.id.etSenha)

        val btnCadastrar = findViewById<MaterialButton>(R.id.btnCadastrar)
        val tvFazerLogin = findViewById<TextView>(R.id.tvFazerLogin)

        // Conexão com o banco Firestore
        val fb = Firebase.firestore

        btnCadastrar.setOnClickListener {

            // 1) Confere se algum campo ficou vazio
            val campos = listOf(etNome, etEmail, etUsuario, etSenha)
            val faltando = campos.any { it.text.isNullOrBlank() }
            if (faltando) {
                showInfoDialog("Preencha todos os campos obrigatórios.")
                return@setOnClickListener
            }

            // 2) Senha precisa ter no mínimo 8 caracteres (igual ao aluno)
            val senha = etSenha.text.toString()
            if (senha.length < 8) {
                showInfoDialog("A senha deve ter no mínimo 8 caracteres.")
                return@setOnClickListener
            }

            // 3) Monta o objeto Admin e salva na coleção "Admins"
            val admin = Admin(
                nome = etNome.text.toString(),
                email = etEmail.text.toString(),
                usuario = etUsuario.text.toString(),
                senha = senha
            )

            fb.collection("Admins")
                .add(admin)
                .addOnSuccessListener {
                    showInfoDialog(getString(R.string.cadastro_sucesso)) {
                        finish() // volta pra tela anterior (Login)
                    }
                }
                .addOnFailureListener {
                    showInfoDialog("Erro ao cadastrar.")
                }
        }

        // Link "Fazer Login" — só fecha esta tela e volta pro Login
        tvFazerLogin.setOnClickListener {
            finish()
        }
    }
}

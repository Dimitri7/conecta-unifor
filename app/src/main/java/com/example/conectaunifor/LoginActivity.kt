package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Tela de Login.
 *
 * O seletor (toggle) ALUNO / ADMINISTRADOR é o ÚNICO lugar onde o usuário
 * escolhe o seu tipo. Tanto o botão ENTRAR quanto o link "Cadastre-se"
 * respeitam essa escolha:
 *   - ALUNO         -> consulta a coleção "Alunos" / abre o cadastro de aluno
 *   - ADMINISTRADOR -> consulta a coleção "Admins" / abre o cadastro de admin
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toggle = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)
        val etId = findViewById<TextInputEditText>(R.id.etId)     // e-mail
        val etPass = findViewById<TextInputEditText>(R.id.etPass) // senha
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        val fb = Firebase.firestore

        btnLogin.setOnClickListener {
            val email = etId.text.toString().trim()
            val senha = etPass.text.toString()

            if (email.isBlank() || senha.isBlank()) {
                showInfoDialog(getString(R.string.login_campos_vazios))
                return@setOnClickListener
            }

            // O toggle decide o tipo e, com isso, a coleção e o "tipo" da sessão.
            val isAdmin = toggle.checkedButtonId == R.id.btnAdmin
            val colecao = if (isAdmin) "Admins" else "Alunos"
            val tipo = if (isAdmin) "admin" else "aluno"

            // Procura no Firestore um documento com aquele email E aquela senha.
            fb.collection(colecao)
                .whereEqualTo("email", email)
                .whereEqualTo("senha", senha)
                .get()
                .addOnSuccessListener { resultado ->
                    if (resultado.isEmpty) {
                        // Não revela se foi o email ou a senha (mais seguro).
                        showInfoDialog(getString(R.string.login_erro_credenciais))
                    } else {
                        val doc = resultado.documents.first()
                        val nome = doc.getString("nome") ?: ""
                        // Salva a sessão e entra no app.
                        SessionManager.salvarSessao(this, doc.id, nome, tipo)
                        irParaTelaPrincipal(tipo)
                    }
                }
                .addOnFailureListener {
                    showInfoDialog(getString(R.string.login_erro_conexao))
                }
        }

        tvForgot.setOnClickListener {
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }

        // O "Cadastre-se" também respeita o toggle.
        tvRegister.setOnClickListener {
            val isAdmin = toggle.checkedButtonId == R.id.btnAdmin
            val destino = if (isAdmin) CadastroAdminActivity::class.java
                          else CadastroActivity::class.java
            startActivity(Intent(this, destino))
        }
    }

    /** Manda o usuário pra tela inicial do seu tipo e fecha o Login. */
    private fun irParaTelaPrincipal(tipo: String) {
        val destino = if (tipo == "admin") AdmEspacosActivity::class.java
                      else AlunoEspacosActivity::class.java
        startActivity(Intent(this, destino))
        finish()
    }
}

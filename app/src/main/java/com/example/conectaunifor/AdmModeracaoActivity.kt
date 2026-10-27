package com.example.conectaunifor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ListenerRegistration

class AdmModeracaoActivity : AppCompatActivity() {

    // "Controle remoto" da escuta em tempo real das denúncias.
    private var registroDenuncias: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_moderacao)
        setupAdminBottomNav(AdminTab.MODERACAO)

        findViewById<MaterialButton>(R.id.btnGerenciarCategorias).setOnClickListener {
            startActivity(Intent(this, AdmCategoriasActivity::class.java))
        }

        // Botão "Sair" (logout): confirma, apaga a sessão e volta pro Login.
        findViewById<TextView>(R.id.tvLogout).setOnClickListener {
            showConfirmDialog(
                message = getString(R.string.confirmar_logout),
                actionLabel = getString(R.string.sair),
                isDanger = true
            ) {
                SessionManager.logout(this)
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            }
        }

        // Liga a escuta em tempo real: novas denúncias aparecem sozinhas.
        registroDenuncias = PostagemRepository.escutarDenuncias(
            onChange = { lista -> mostrarDenuncias(lista) },
            onError = { showInfoDialog("Erro ao carregar as denúncias.") }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        registroDenuncias?.remove() // desliga a escuta
    }

    private fun mostrarDenuncias(lista: List<Denuncia>) {
        val container = findViewById<LinearLayout>(R.id.listaContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        if (lista.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Nenhuma denúncia ativa no momento."
                setTextColor(ContextCompat.getColor(this@AdmModeracaoActivity, R.color.colorTextMuted))
                textSize = 14f
                setPadding(8, 24, 8, 8)
            }
            container.addView(tv)
            return
        }

        for (d in lista) {
            val view = inflater.inflate(R.layout.item_denuncia, container, false)
            view.findViewById<TextView>(R.id.tvMotivo).text = "Motivo: ${d.motivo}"
            view.findViewById<TextView>(R.id.tvPostOriginal).text =
                "Post original: \"${d.postagemConteudo}\""
            view.findViewById<TextView>(R.id.tvAutor).text = "- Autor: ${d.postagemAutorNome}"

            // IGNORAR: apaga só a denúncia; o post continua.
            view.findViewById<MaterialButton>(R.id.btnIgnorar).setOnClickListener {
                PostagemRepository.removerDenuncia(
                    d.id,
                    onSuccess = { /* a escuta atualiza a lista sozinha */ },
                    onError = { showInfoDialog("Não foi possível ignorar agora.") }
                )
            }

            // REMOVER POST: apaga o post E a denúncia.
            view.findViewById<MaterialButton>(R.id.btnRemover).setOnClickListener {
                showConfirmDialog(
                    message = getString(R.string.confirmar_remover_post),
                    actionLabel = getString(R.string.confirmar),
                    isDanger = true
                ) {
                    // O admin sempre tem permissão de excluir (checado no repositório).
                    // autorId vazio aqui pois a regra "é admin" já libera a exclusão.
                    PostagemRepository.excluirPostagem(
                        this, d.postagemId, "",
                        onSuccess = {
                            // Apagou o post -> agora apaga a denúncia também.
                            PostagemRepository.removerDenuncia(
                                d.id,
                                onSuccess = { /* lista atualiza sozinha */ },
                                onError = { showInfoDialog("Post removido, mas falhou ao limpar a denúncia.") }
                            )
                        },
                        onError = { showInfoDialog("Não foi possível remover o post agora.") }
                    )
                }
            }

            container.addView(view)
        }
    }
}

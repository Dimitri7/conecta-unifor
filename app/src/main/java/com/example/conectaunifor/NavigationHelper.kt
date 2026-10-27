package com.example.conectaunifor

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat

enum class AlunoTab { ESPACOS, ALUGUEL, FEED, PERFIL }
enum class AdminTab { ESPACOS, REPORTS, MODERACAO }

/**
 * Liga os 4 itens do bottom nav do aluno e marca o item atual como selecionado.
 * Usa flags CLEAR_TOP + SINGLE_TOP para não empilhar instâncias da mesma tela.
 */
fun Activity.setupAlunoBottomNav(current: AlunoTab) {
    val navEspacos = findViewById<TextView?>(R.id.navEspacos)
    val navAluguel = findViewById<TextView?>(R.id.navAluguel)
    val navFeed = findViewById<TextView?>(R.id.navFeed)
    val navPerfil = findViewById<TextView?>(R.id.navPerfil)

    fun mark(tv: TextView?, selected: Boolean) {
        tv ?: return
        tv.setTextColor(
            ContextCompat.getColor(
                this,
                if (selected) R.color.colorTextPrimary else R.color.colorTextMuted
            )
        )
        tv.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
    }
    mark(navEspacos, current == AlunoTab.ESPACOS)
    mark(navAluguel, current == AlunoTab.ALUGUEL)
    mark(navFeed, current == AlunoTab.FEED)
    mark(navPerfil, current == AlunoTab.PERFIL)

    fun goTo(target: AlunoTab, cls: Class<*>) {
        if (current == target) return
        val intent = Intent(this, cls).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
    navEspacos?.setOnClickListener { goTo(AlunoTab.ESPACOS, AlunoEspacosActivity::class.java) }
    navAluguel?.setOnClickListener { goTo(AlunoTab.ALUGUEL, AlunoAluguelActivity::class.java) }
    navFeed?.setOnClickListener { goTo(AlunoTab.FEED, AlunoFeedActivity::class.java) }
    navPerfil?.setOnClickListener { goTo(AlunoTab.PERFIL, AlunoPerfilActivity::class.java) }
}

fun Activity.setupAdminBottomNav(current: AdminTab) {
    val navEspacos = findViewById<TextView?>(R.id.navEspacos)
    val navReports = findViewById<TextView?>(R.id.navReports)
    val navModeracao = findViewById<TextView?>(R.id.navModeracao)

    fun mark(tv: TextView?, selected: Boolean) {
        tv ?: return
        tv.setTextColor(
            ContextCompat.getColor(
                this,
                if (selected) R.color.colorTextPrimary else R.color.colorTextMuted
            )
        )
        tv.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
    }
    mark(navEspacos, current == AdminTab.ESPACOS)
    mark(navReports, current == AdminTab.REPORTS)
    mark(navModeracao, current == AdminTab.MODERACAO)

    fun goTo(target: AdminTab, cls: Class<*>) {
        if (current == target) return
        val intent = Intent(this, cls).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
    navEspacos?.setOnClickListener { goTo(AdminTab.ESPACOS, AdmEspacosActivity::class.java) }
    navReports?.setOnClickListener { goTo(AdminTab.REPORTS, AdmReportsActivity::class.java) }
    navModeracao?.setOnClickListener { goTo(AdminTab.MODERACAO, AdmModeracaoActivity::class.java) }
}

package com.example.conectaunifor

import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
 * Diálogo de confirmação padronizado (Cancelar / botão de ação).
 * actionLabel é o texto do botão de ação (ex: "Excluir", "Interditar").
 * isDanger pinta o botão de ação em vermelho.
 */
fun Context.showConfirmDialog(
    message: String,
    actionLabel: String,
    isDanger: Boolean = true,
    onConfirm: () -> Unit
) {
    val dialog = AlertDialog.Builder(this)
        .setMessage(message)
        .setNegativeButton(R.string.cancelar) { d, _ -> d.dismiss() }
        .setPositiveButton(actionLabel) { d, _ ->
            d.dismiss()
            onConfirm()
        }
        .create()
    dialog.setOnShowListener {
        if (isDanger) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.colorDanger))
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.colorTextSecondary))
    }
    dialog.show()
}

/**
 * Mensagem de sucesso simples com 1 botão "OK".
 */
fun Context.showInfoDialog(
    message: String,
    onDismiss: (() -> Unit)? = null
) {
    val dialog = AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton(R.string.ok) { d, _ ->
            d.dismiss()
            onDismiss?.invoke()
        }
        .create()
    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.colorPrimary))
    }
    dialog.show()
}

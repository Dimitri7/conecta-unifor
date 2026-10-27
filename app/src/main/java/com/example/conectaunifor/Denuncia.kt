package com.example.conectaunifor

import com.google.firebase.Timestamp

/**
 * Uma denúncia de uma postagem, guardada no Firestore (coleção "denuncias").
 * Todos os campos têm valor padrão para o Firestore conseguir LER (toObject).
 *
 * Repare nos campos "postagem*": eles são uma CÓPIA dos dados do post no momento
 * da denúncia (chamamos isso de "denormalizar"). Assim a tela de Moderação
 * consegue mostrar do que se trata MESMO se o post original já tiver sido apagado
 * antes do admin analisar. (Detalhes no docs/FASE_3_5_DENUNCIAS.md.)
 */
data class Denuncia(
    val id: String = "",                 // código do documento no Firestore
    val postagemId: String = "",         // id da postagem denunciada
    val postagemTitulo: String = "",     // cópia do título (denormalizado)
    val postagemConteudo: String = "",   // cópia do conteúdo (denormalizado)
    val postagemAutorNome: String = "",  // cópia do autor (denormalizado)
    val motivo: String = "",
    val denuncianteId: String = "",      // quem denunciou
    val denuncianteNome: String = "",
    val dataCriacao: Timestamp? = null
)

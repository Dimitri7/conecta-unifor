package com.example.conectaunifor

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Ponto único que conversa com o Firestore para o FEED.
 *
 * É um "singleton" (object): uma só cópia no app inteiro. Assim qualquer tela
 * chama PostagemRepository.algumaCoisa() e fala com o mesmo lugar.
 *
 * Estrutura no banco:
 *   postagens (coleção)
 *     └── {idDoPost} (documento)
 *           ├── autorId, autorNome, titulo, conteudo, categoria
 *           ├── dataCriacao (Timestamp), curtidasIds (lista), qtdComentarios (número)
 *           └── comentarios (subcoleção)
 *                 └── {idDoComentario} -> autorId, autorNome, conteudo, dataCriacao
 */
object PostagemRepository {

    private const val POSTAGENS = "postagens"
    private const val COMENTARIOS = "comentarios"
    private const val DENUNCIAS = "denuncias"

    private val db get() = Firebase.firestore

    // ---------- POSTAGENS ----------

    /**
     * "Escuta" a lista de postagens em TEMPO REAL (snapshot listener).
     * Toda vez que algo muda no banco, onChange é chamado de novo com a lista atualizada.
     * Devolve um "controle remoto" (ListenerRegistration) para a tela DESLIGAR a escuta depois.
     */
    fun escutarPostagens(
        onChange: (List<Postagem>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection(POSTAGENS)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    onError(erro); return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Postagem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onChange(lista)
            }
    }

    /** Lê UMA vez as postagens de um autor (usado no Perfil). Ordena por data no app. */
    fun buscarPostagensDoAutor(
        autorId: String,
        onSuccess: (List<Postagem>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(POSTAGENS)
            .whereEqualTo("autorId", autorId)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents
                    .mapNotNull { doc -> doc.toObject(Postagem::class.java)?.copy(id = doc.id) }
                    .sortedByDescending { it.dataCriacao?.seconds ?: 0L }
                onSuccess(lista)
            }
            .addOnFailureListener { onError(it) }
    }

    /** Lê UMA postagem pelo id (usado na tela de Detalhe e Editar). */
    fun buscarPostagem(
        id: String,
        onSuccess: (Postagem?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(POSTAGENS).document(id).get()
            .addOnSuccessListener { doc ->
                onSuccess(doc.toObject(Postagem::class.java)?.copy(id = doc.id))
            }
            .addOnFailureListener { onError(it) }
    }

    /** Cria uma nova postagem. A data é o momento atual (Timestamp.now()). */
    fun criarPostagem(
        p: Postagem,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val dados = hashMapOf(
            "autorId" to p.autorId,
            "autorNome" to p.autorNome,
            "titulo" to p.titulo,
            "conteudo" to p.conteudo,
            "categoria" to p.categoria,
            "dataCriacao" to Timestamp.now(),
            "curtidasIds" to emptyList<String>(),
            "qtdComentarios" to 0
        )
        db.collection(POSTAGENS).add(dados)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    /** Atualiza só os campos editáveis (NÃO recria o documento, mantém o mesmo id/curtidas/comentários). */
    fun atualizarPostagem(
        id: String,
        titulo: String,
        conteudo: String,
        categoria: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(POSTAGENS).document(id)
            .update(
                mapOf(
                    "titulo" to titulo,
                    "conteudo" to conteudo,
                    "categoria" to categoria
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    /**
     * Exclui uma postagem, mas SÓ se quem pediu for o AUTOR ou um ADMIN.
     * A checagem usa a sessão salva (SessionManager).
     */
    fun excluirPostagem(
        context: Context,
        id: String,
        autorId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val usuario = SessionManager.getUsuarioLogado(context)
        val podeExcluir = usuario != null && (usuario.id == autorId || usuario.tipo == "admin")
        if (!podeExcluir) {
            onError(SecurityException("Sem permissão para excluir esta postagem."))
            return
        }
        db.collection(POSTAGENS).document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    /**
     * Curtir/descurtir: se o usuário ainda não curtiu, adiciona o id dele na lista
     * (arrayUnion); se já curtiu, remove (arrayRemove). Lê o estado atual antes pra decidir.
     */
    fun alternarCurtida(
        postagemId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = db.collection(POSTAGENS).document(postagemId)
        ref.get()
            .addOnSuccessListener { doc ->
                @Suppress("UNCHECKED_CAST")
                val curtidas = (doc.get("curtidasIds") as? List<String>) ?: emptyList()
                val operacao =
                    if (curtidas.contains(userId)) FieldValue.arrayRemove(userId)
                    else FieldValue.arrayUnion(userId)
                ref.update("curtidasIds", operacao)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it) }
            }
            .addOnFailureListener { onError(it) }
    }

    // ---------- COMENTÁRIOS (subcoleção) ----------

    /** Escuta os comentários de uma postagem em tempo real, dos mais antigos pros mais novos. */
    fun escutarComentarios(
        postagemId: String,
        onChange: (List<Comentario>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection(POSTAGENS).document(postagemId).collection(COMENTARIOS)
            .orderBy("dataCriacao", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    onError(erro); return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Comentario::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onChange(lista)
            }
    }

    /**
     * Adiciona um comentário na subcoleção E soma +1 no contador qtdComentarios da postagem
     * (FieldValue.increment(1)).
     */
    fun adicionarComentario(
        postagemId: String,
        c: Comentario,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val postRef = db.collection(POSTAGENS).document(postagemId)
        val dados = hashMapOf(
            "autorId" to c.autorId,
            "autorNome" to c.autorNome,
            "conteudo" to c.conteudo,
            "dataCriacao" to Timestamp.now()
        )
        postRef.collection(COMENTARIOS).add(dados)
            .addOnSuccessListener {
                postRef.update("qtdComentarios", FieldValue.increment(1))
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it) }
            }
            .addOnFailureListener { onError(it) }
    }

    // ---------- DENÚNCIAS ----------

    /**
     * Cria uma denúncia. Copiamos (denormalizamos) título/conteúdo/autor do post
     * para a denúncia continuar legível mesmo se o post for excluído depois.
     */
    fun criarDenuncia(
        d: Denuncia,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val dados = hashMapOf(
            "postagemId" to d.postagemId,
            "postagemTitulo" to d.postagemTitulo,
            "postagemConteudo" to d.postagemConteudo,
            "postagemAutorNome" to d.postagemAutorNome,
            "motivo" to d.motivo,
            "denuncianteId" to d.denuncianteId,
            "denuncianteNome" to d.denuncianteNome,
            "dataCriacao" to Timestamp.now()
        )
        db.collection(DENUNCIAS).add(dados)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    /** Escuta as denúncias em tempo real (mais novas primeiro), para a tela de Moderação. */
    fun escutarDenuncias(
        onChange: (List<Denuncia>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection(DENUNCIAS)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    onError(erro); return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Denuncia::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onChange(lista)
            }
    }

    /** Remove uma denúncia (usado tanto no IGNORAR quanto após o REMOVER POST). */
    fun removerDenuncia(
        denunciaId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(DENUNCIAS).document(denunciaId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}

/** Transforma um Timestamp do Firestore em texto curto "dd/MM" (ou "" se for nulo). */
fun formatarData(ts: Timestamp?): String {
    if (ts == null) return ""
    val formato = SimpleDateFormat("dd/MM", Locale.forLanguageTag("pt-BR"))
    return formato.format(ts.toDate())
}

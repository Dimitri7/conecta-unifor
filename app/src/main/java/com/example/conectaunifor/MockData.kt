package com.example.conectaunifor

import com.google.firebase.Timestamp

/**
 * Dados mockados em memória — servem para o app rodar sem backend.
 * Em produção, substituir por Room/API.
 *
 * OBS (Fase 3): o FEED foi migrado para o Firestore. Por isso a estrutura de
 * Postagem/Comentario aqui foi adaptada ao Firestore e as listas mock de
 * postagens ficaram VAZIAS (os dados de verdade vêm do banco, via
 * PostagemRepository). As listas de cápsulas/salas/livros/aluguéis continuam
 * mockadas (são de outras features e não foram tocadas).
 */

enum class StatusEspaco { LIVRE, OCUPADA, INDISPONIVEL }
enum class TipoEspaco { CAPSULA, SALA }

data class Espaco(
    val id: Int,
    var nome: String,
    var status: StatusEspaco,
    var local: String,
    var capacidade: Int,
    var descricao: String,
    val tipo: TipoEspaco
)

data class Livro(
    val id: Int,
    var titulo: String,
    var autor: String,
    var genero: String,
    var status: StatusEspaco,
    val descricao: String
)

/**
 * Uma postagem do Feed, no formato do Firestore (coleção "postagens").
 * Todos os campos têm valor padrão para o Firestore conseguir LER (toObject).
 *
 * - id: o código do documento no Firestore (preenchido ao ler, não fica salvo dentro do doc).
 * - autorId / autorNome: quem criou (id da sessão + nome para exibir).
 * - dataCriacao: data/hora que o Firestore entende (Timestamp).
 * - curtidasIds: lista dos ids de quem curtiu (o nº de curtidas é o tamanho da lista).
 * - qtdComentarios: contador de comentários (os comentários ficam numa subcoleção).
 */
data class Postagem(
    val id: String = "",
    val autorId: String = "",
    val autorNome: String = "",
    val titulo: String = "",
    val conteudo: String = "",
    val categoria: String = "",
    val dataCriacao: Timestamp? = null,
    val curtidasIds: List<String> = emptyList(),
    val qtdComentarios: Int = 0
)

/**
 * Um comentário de uma postagem (subcoleção "comentarios" dentro de cada postagem).
 */
data class Comentario(
    val id: String = "",
    val autorId: String = "",
    val autorNome: String = "",
    val conteudo: String = "",
    val dataCriacao: Timestamp? = null
)

data class Aluguel(
    val id: Int,
    val livroTitulo: String,
    val alunoNome: String,
    val prazo: String
)

data class Reporte(
    val id: Int,
    val espacoNome: String,
    val descricao: String,
    val relator: String
)

// A Denuncia agora é um modelo do Firestore (ver Denuncia.kt). Migrada na Fase 3.5.

object MockRepository {

    val espacos: MutableList<Espaco> = mutableListOf(
        Espaco(1, "Cápsula 01", StatusEspaco.LIVRE, "1º Andar", 1,
            "Cápsula individual com isolamento acústico e tomada 220v.", TipoEspaco.CAPSULA),
        Espaco(2, "Cápsula 02", StatusEspaco.OCUPADA, "1º Andar", 1,
            "Cápsula individual com tomada 220v.", TipoEspaco.CAPSULA),
        Espaco(3, "Cápsula 03", StatusEspaco.INDISPONIVEL, "2º Andar", 1,
            "Em manutenção.", TipoEspaco.CAPSULA),
        Espaco(4, "Sala K01", StatusEspaco.LIVRE, "1º Andar", 5,
            "Sala em grupo com mesa central.", TipoEspaco.SALA),
        Espaco(5, "Sala K02", StatusEspaco.OCUPADA, "1º Andar", 5,
            "Sala em grupo com TV.", TipoEspaco.SALA),
        Espaco(6, "Sala K03", StatusEspaco.INDISPONIVEL, "1º Andar", 5,
            "Em reforma.", TipoEspaco.SALA)
    )

    val livros: MutableList<Livro> = mutableListOf(
        Livro(1, "Clean Code", "Robert C. Martin", "Tecnologia", StatusEspaco.INDISPONIVEL,
            "Livro essencial sobre boas práticas de programação e código limpo."),
        Livro(2, "Java para Iniciantes", "Herbert Schildt", "Tecnologia", StatusEspaco.LIVRE,
            "Introdução completa à linguagem Java."),
        Livro(3, "Design Patterns", "Gang of Four", "Tecnologia", StatusEspaco.LIVRE,
            "Catálogo dos padrões clássicos de projeto orientado a objetos.")
    )

    // Vazias: o Feed agora vem do Firestore (ver PostagemRepository).
    val postagens: MutableList<Postagem> = mutableListOf()

    // "Posts salvos" não foi migrado nesta fase (limitação conhecida): fica vazio.
    val postagensSalvas: MutableList<Postagem> = mutableListOf()

    val livrosDesejados: MutableList<Livro> = mutableListOf(livros[2])
    val livrosAlugados: MutableList<Aluguel> = mutableListOf(
        Aluguel(1, "Clean Code", "Dimitri", "30/03")
    )

    val categorias: MutableList<String> = mutableListOf("Dúvidas", "Resenhas", "Eventos")

    val reportes: MutableList<Reporte> = mutableListOf(
        Reporte(1, "Cápsula 01",
            "A tomada 220v está com mau contato e não carrega o notebook.",
            "João")
    )

    // Vazia: as denúncias agora vêm do Firestore (ver PostagemRepository). Migrado na Fase 3.5.
    val denuncias: MutableList<Denuncia> = mutableListOf()

    val solicitacoesAluguel: MutableList<Aluguel> = mutableListOf(
        Aluguel(2, "Clean Code", "Dimitri", "—")
    )

    fun proximoIdEspaco(): Int = (espacos.maxOfOrNull { it.id } ?: 0) + 1
    fun proximoIdLivro(): Int = (livros.maxOfOrNull { it.id } ?: 0) + 1
    // proximoIdPostagem() removido: o id da postagem agora é gerado pelo Firestore.
}

fun StatusEspaco.label(): String = when (this) {
    StatusEspaco.LIVRE -> "LIVRE"
    StatusEspaco.OCUPADA -> "OCUPADA"
    StatusEspaco.INDISPONIVEL -> "INDISPONÍVEL"
}

fun StatusEspaco.colorResId(): Int = when (this) {
    StatusEspaco.LIVRE -> R.color.colorSuccess
    StatusEspaco.OCUPADA -> R.color.colorDanger
    StatusEspaco.INDISPONIVEL -> R.color.colorTextMuted
}

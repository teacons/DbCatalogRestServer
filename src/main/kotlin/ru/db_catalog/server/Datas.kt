package ru.db_catalog.server


data class Content(
    val id: Long,
    val name: String,
    val year: Int,
    val poster: String?,
    val rating: Double,
    val genres: Set<String>,
)

data class ContentIdName(val id: Long, val name: String)

data class AuthResponse(val token: String?, val role: Long?)

enum class ContentType {
    BOOK, FILM, MUSIC
}

data class ErrorCode(val code: Long)

fun getSlice(
    id: Long?,
    ids: List<Long>,
    size: Int
): List<Long> {
    return if (id != null) {
        val fromId = ids.indexOf(id)
        val toId = if (fromId + 1 + size <= ids.size) fromId + 1 + size else ids.size
        ids.subList(fromId + 1, toId)
    } else {
        val toId = if (size <= ids.size) size else ids.size
        ids.subList(0, toId)
    }
}
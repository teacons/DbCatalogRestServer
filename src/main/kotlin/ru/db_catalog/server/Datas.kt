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

data class AuthResponse(val token: String?)
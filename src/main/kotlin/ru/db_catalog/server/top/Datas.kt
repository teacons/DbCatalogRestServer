package ru.db_catalog.server.top

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("top")
data class BookTop(
    @Id val id: Long?, val name: String,
    @MappedCollection(idColumn = "top_id", keyColumn = "top_id")
    val content: MutableSet<BookTopRef> = mutableSetOf()
)

@Table("top_has_book")
data class BookTopRef(@Column("book_id") val id: Long, val position: Int)

@Table("top")
data class FilmTop(
    @Id val id: Long?, val name: String,
    @MappedCollection(idColumn = "top_id")
    val content: MutableSet<FilmTopRef> = mutableSetOf(),
)

@Table("top_has_film")
data class FilmTopRef(@Column("film_id") val id: Long, val position: Int)

@Table("top")
data class MusicTop(
    @Id val id: Long?, val name: String,
    @MappedCollection(idColumn = "top_id")
    val content: MutableSet<MusicTopRef> = mutableSetOf(),
)

@Table("top_has_music")
data class MusicTopRef(@Column("music_id") val id: Long, val position: Int)
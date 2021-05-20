package ru.db_catalog.server.top

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("top")
data class BookTop(
    @Id val id: Long, val name: String,
    @MappedCollection(idColumn = "top_id", keyColumn = "top_id")
    val books: MutableSet<BookTopRef> = mutableSetOf()
)

@Table("top_has_book")
data class BookTopRef(val bookId: Long, val position: Int)

data class TopIdName(val id: Long, val name: String)

@Table("top")
data class FilmTop(
    @Id val id: Long, val name: String,
    @MappedCollection(idColumn = "top_id")
    val films: MutableSet<FilmTopRef> = mutableSetOf(),
)

@Table("top_has_film")
data class FilmTopRef(val filmId: Long, val position: Int)

@Table("top")
data class MusicTop(
    @Id val id: Long, val name: String,
    @MappedCollection(idColumn = "top_id")
    val musics: MutableSet<MusicTopRef> = mutableSetOf(),
)

@Table("top_has_music")
data class MusicTopRef(val musicId: Long, val position: Int)
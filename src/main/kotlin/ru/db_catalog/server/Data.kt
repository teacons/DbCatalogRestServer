package ru.db_catalog.server

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("music_genre")
data class MusicGenre(@Id val id: Long?, val name: String, val description: String)

@Table("film_genre")
data class FilmGenre(@Id val id: Long?, val name: String, val description: String)

@Table("book_genre")
data class BookGenre(@Id val id: Long?, val name: String, val description: String)

@Table("book")
data class Book(
    @Id val id: Long?,
    val name: String,
    val year: Int,
    val description: String,
    val poster: String?,
    val bookSeriesId: Long
)

@Table("book_series")
data class BookSeries(
    @Id val id: Long?, val name: String, val description: String?,
    @MappedCollection(idColumn = "book_series_id", keyColumn = "book_series_id")
    val books: List<Book>
)


data class BookWithSeries(
    val id: Long?,
    val name: String,
    val year: Int,
    val description: String,
    val poster: String?,
    val bookSeries: BookSeriesWithoutBooks
)

data class BookSeriesWithoutBooks(val id: Long?, val name: String, val description: String?)


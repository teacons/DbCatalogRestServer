package ru.db_catalog.server

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("music_genre")
data class MusicGenre(@Id val id: Long, val name: String, val description: String)

@Table("film_genre")
data class FilmGenre(@Id val id: Long, val name: String, val description: String)

@Table("book_genre")
data class BookGenre(@Id val id: Long, val name: String, val description: String)

@Table("book")
data class Book(
    @Id val id: Long,
    val name: String,
    val year: Int,
    val description: String,
    val poster: String?,
    val bookSeriesId: Long?,
    @MappedCollection(idColumn = "book_id")
    val bookGenres: MutableSet<BookGenreRef> = mutableSetOf(),
    @MappedCollection(idColumn = "book_id")
    val authors: MutableSet<AuthorRef> = mutableSetOf()
) {
    fun addBookGenre(bookGenre: BookGenre) {
        bookGenres.add(BookGenreRef(bookGenre.id))
    }

    fun addAuthor(author: People) {
        authors.add(AuthorRef(author.id))
    }
}

@Table("book_series")
data class BookSeries(
    @Id val id: Long, val name: String, val description: String?,
    @MappedCollection(idColumn = "book_series_id", keyColumn = "book_series_id")
    val books: List<Book>
)

@Table("book_has_book_genre")
data class BookGenreRef(val bookGenreId: Long)

@Table("book_has_people")
data class AuthorRef(val peopleId: Long)


data class BookWithSeries(
    val id: Long,
    val name: String,
    val year: Int,
    val description: String,
    val poster: String?,
    val bookGenres: Set<BookGenre>,
    val authors: Set<People>,
    val bookSeries: BookSeriesWithoutBooks?

)

data class BookSeriesWithoutBooks(val id: Long, val name: String, val description: String?)

@Table("people")
data class People(@Id val id: Long, val fullname: String, val yearOfBirth: Int?)


package ru.db_catalog.server.book

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.people.People
import java.util.*

@Table("book_genre")
data class BookGenre(@Id val id: Long, val name: String, val description: String)

@Table("book")
data class Book(
    @Id val id: Long?,
    val name: String,
    val year: Int,
    val description: String,
    val poster: String?,
    val bookSeriesId: Long?,
    @MappedCollection(idColumn = "book_id")
    val bookGenres: Set<BookGenreRef>,
    @MappedCollection(idColumn = "book_id")
    val authors: Set<BookAuthorRef>
)

@Table("book_series")
data class BookSeries(
    @Id val id: Long?, val name: String, val description: String?,
    @MappedCollection(idColumn = "book_series_id", keyColumn = "book_series_id")
    val books: Set<Book>
)

@Table("book_has_book_genre")
data class BookGenreRef(val bookGenreId: Long)

@Table("book_has_people")
data class BookAuthorRef(val peopleId: Long)

data class BookForAnswer(
    val id: Long,
    val name: String,
    val year: Int,
    val description: String,
    val poster: String?,
    val rating: Double,
    val bookSeries: ContentIdName?,
    val authors: Set<People>,
    val genres: Set<String>,
    val viewed: Boolean,
    val ratingUser: Double?,
    val top: ContentIdName?,
    val topPosition: Int?
)

data class FilterBook(
    val genres: Set<Long>?,
    val authors: Set<Long>?,
    val years: Pair<Int, Int>,
    val ratings: Pair<Int, Int>
) {
    fun getUUID(): String {
        val genresString = genres?.joinToString()
        val authorsString = authors?.joinToString()
        val yearsString = "${years.first} - ${years.second}"
        val ratingsString = "${ratings.first} - ${ratings.second}"

        val string = "${genresString}_${authorsString}_${yearsString}_${ratingsString}"

        val uuid = UUID.nameUUIDFromBytes(string.toByteArray())

        return uuid.toString()
    }
}
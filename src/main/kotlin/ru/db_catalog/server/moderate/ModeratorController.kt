package ru.db_catalog.server.moderate

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.book.*
import ru.db_catalog.server.people.People
import ru.db_catalog.server.people.PeopleService

@RestController
@RequestMapping("/api/moderate")
class ModeratorController(
    val bookService: BookService,
    val peopleService: PeopleService
) {

    @PostMapping("/book")
    fun addBook(
        @RequestParam(value = "name", required = true) name: String,
        @RequestParam(value = "year", required = true) year: Int,
        @RequestParam(value = "description", required = true) description: String,
        @RequestParam(value = "poster", required = false) poster: String?,
        @RequestParam(value = "book_series", required = false) bookSeriesFrom: String?,
        @RequestParam(value = "authors", required = true) authorsFrom: Set<String>,
        @RequestParam(value = "genres", required = true) genresFrom: Set<Long>
    ): ResponseEntity<Any> {

        val bookSeriesId = if (bookSeriesFrom != null) {
            var bookSeries = bookService.findBookSeriesByName(bookSeriesFrom)

            if (bookSeries == null) {
                bookSeries = BookSeries(null, bookSeriesFrom, null, emptySet())

                bookSeries = bookService.saveBookSeries(bookSeries)
            }

            bookSeries.id
        } else null

        val authors = peopleService.findAllByFullnameIn(authorsFrom).toMutableSet()

        if (authors.size != authorsFrom.size) {
            val diff = mutableSetOf<String>()
            authorsFrom.forEach { s ->
                if (s !in authors.map { it.fullname }) diff.add(s)
            }

            diff.forEach {
                authors.add(peopleService.savePeople(People(null, it, null)))
            }
        }

        val authorsRefs = mutableSetOf<BookAuthorRef>()

        authors.forEach {
            it.id?.let { id -> authorsRefs.add(BookAuthorRef(id)) }
        }

        val genres = bookService.findBookGenresByIds(genresFrom)

        val genresRefs = mutableSetOf<BookGenreRef>()

        genres.forEach {
            genresRefs.add(BookGenreRef(it.id))
        }

        var book = Book(null, name, year, description, poster, bookSeriesId, genresRefs, authorsRefs)

        book = bookService.saveBook(book)

        return ResponseEntity(book.id, HttpStatus.OK)

    }
}
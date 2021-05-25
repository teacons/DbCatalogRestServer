package ru.db_catalog.server.book

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.Content
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.people.People
import ru.db_catalog.server.people.PeopleService
import ru.db_catalog.server.top.BookTopService
import ru.db_catalog.server.top.TopIdName
import ru.db_catalog.server.user.UserService

@RestController
@RequestMapping("/api/book")
class BookController(
    val bookService: BookService,
    val peopleService: PeopleService,
    val bookTopService: BookTopService,
    val userService: UserService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val userId = userService.findByUsername(username)?.id
        return prepareBook(bookService.findBookById(id).get(), expanded, userId)
    }

    @GetMapping
    fun getBooks(): Set<ContentIdName> = bookService.findAllBookIdName()

    fun prepareBook(book: Book, expanded: Boolean, userId: Long?): ResponseEntity<Any> {

        var rating = bookService.getBookRating(book.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        book.bookGenres.forEach {
            genres.add(bookService.findBookGenreById(it.bookGenreId).get().name)
        }

        if (!expanded) {
            return ResponseEntity(Content(book.id, book.name, book.year, book.poster, rating, genres), HttpStatus.OK)
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)
            val bookSeriesWithoutBooks = if (book.bookSeriesId != null) {
                val bookSeries = bookService.findBookSeriesById(book.bookSeriesId).get()

                ContentIdName(bookSeries.id, bookSeries.name)
            } else null

            val authors = mutableSetOf<People>()

            book.authors.forEach {
                authors.add(peopleService.findPeopleById(it.peopleId).get())
            }

            val viewed = userService.existsViewByUserIdBookId(userId, book.id)

            val userRating = userService.getUserBookRating(userId, book.id)

            val top = bookTopService.findByBookId(book.id).firstOrNull()
            val topIdName = top?.let { TopIdName(it.id, it.name) }
            val topPosition = top?.let { bookTopService.findPositionInTop(it.id, book.id) }

            return ResponseEntity(
                BookForAnswer(
                    book.id,
                    book.name,
                    book.year,
                    book.description,
                    book.poster,
                    rating,
                    bookSeriesWithoutBooks,
                    authors,
                    genres,
                    viewed,
                    userRating,
                    topIdName,
                    topPosition
                ), HttpStatus.OK
            )

        }

    }
}
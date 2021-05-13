package ru.db_catalog.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

@RestController
@RequestMapping("/api/music_genre")
class MusicGenreController(val service: MusicGenreService) {

    @GetMapping
    fun getMusicGenres(): MutableIterable<MusicGenre> = service.getMusicGenres()

    @GetMapping("/{id}")
    fun getMusicGenre(@PathVariable id: Long): Optional<MusicGenre> = service.getMusicGenre(id)

}

@RestController
@RequestMapping("/api/film_genre")
class FilmGenreController(val service: FilmGenreService) {

    @GetMapping
    fun getFilmGenres(): MutableIterable<FilmGenre> = service.getFilmGenres()

    @GetMapping("/{id}")
    fun getFilmGenre(@PathVariable id: Long): Optional<FilmGenre> = service.getFilmGenre(id)

}

@RestController
@RequestMapping("/api/book_genre")
class BookGenreController(val service: BookGenreService) {

    @GetMapping
    fun getBookGenres(): MutableIterable<BookGenre> = service.getBookGenres()

    @GetMapping("/{id}")
    fun getBookGenre(@PathVariable id: Long): Optional<BookGenre> = service.getBookGenre(id)

}

@RestController
@RequestMapping("/api/book")
class BookController(
    val bookService: BookService,
    val bookSeriesService: BookSeriesService,
    val bookGenreService: BookGenreService,
    val peopleService: PeopleService
) {

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): BookWithSeries = prepareBook(bookService.findById(id).get())

    @GetMapping
    fun getBooks(): List<BookWithSeries> {
        val booksWithSeries = mutableListOf<BookWithSeries>()

        bookService.findAll().forEach {
            booksWithSeries.add(prepareBook(it))
        }

        return booksWithSeries
    }

    fun prepareBook(book: Book): BookWithSeries {
        val bookSeriesWithoutBooks = if (book.bookSeriesId != null) {
            val bookSeries = bookSeriesService.findById(book.bookSeriesId).get()

            BookSeriesWithoutBooks(bookSeries.id, bookSeries.name, bookSeries.description)
        } else null

        val bookGenres = mutableSetOf<BookGenre>()

        book.bookGenres.forEach {
            bookGenres.add(bookGenreService.getBookGenre(it.bookGenreId).get())
        }

        val authors = mutableSetOf<People>()

        book.authors.forEach{
            authors.add(peopleService.findById(it.peopleId).get())
        }


        return BookWithSeries(
            book.id,
            book.name,
            book.year,
            book.description,
            book.poster,
            bookGenres,
            authors,
            bookSeriesWithoutBooks
        )
    }

}



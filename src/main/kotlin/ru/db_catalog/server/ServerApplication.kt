package ru.db_catalog.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

@RestController
@RequestMapping("/api/music_genre")
class MusicGenreController(val service: MusicGenreService) {

    @GetMapping
    fun getMusicGenres(): List<MusicGenre> = service.getMusicGenres()

}

@RestController
@RequestMapping("/api/film_genre")
class FilmGenreController(val service: FilmGenreService) {

    @GetMapping
    fun getFilmGenres(): List<FilmGenre> = service.getFilmGenres()

}

@RestController
@RequestMapping("/api/book_genre")
class BookGenreController(val service: BookGenreService) {

    @GetMapping
    fun getBookGenres(): List<BookGenre> = service.getBookGenres()

}

@RestController
@RequestMapping("/api/book")
class BookController(val bookService: BookService, val bookSeriesService: BookSeriesService) {

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): BookWithSeries {

        val book = bookService.findById(id).get()
        val bookSeriesWithoutBooks = if (book.bookSeriesId != null) {
            val bookSeries = bookSeriesService.findById(book.bookSeriesId).get()

            BookSeriesWithoutBooks(bookSeries.id, bookSeries.name, bookSeries.description)
        } else null
        return BookWithSeries(book.id, book.name, book.year, book.description, book.poster, bookSeriesWithoutBooks)
    }

    @GetMapping
    fun getBooks(): List<BookWithSeries> {
        val booksWithSeries = mutableListOf<BookWithSeries>()

        bookService.findAll().forEach {
            val bookSeriesWithoutBooks = if (it.bookSeriesId != null) {
                val bookSeries = bookSeriesService.findById(it.bookSeriesId).get()

                BookSeriesWithoutBooks(bookSeries.id, bookSeries.name, bookSeries.description)
            } else null
            booksWithSeries.add(BookWithSeries(it.id, it.name, it.year, it.description, it.poster, bookSeriesWithoutBooks))
        }

        return booksWithSeries
    }

}



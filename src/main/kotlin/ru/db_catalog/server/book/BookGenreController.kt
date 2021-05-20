package ru.db_catalog.server.book

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/book_genre")
class BookGenreController(val service: BookGenreService) {

    @GetMapping
    fun getBookGenres(): MutableIterable<BookGenre> = service.getBookGenres()

    @GetMapping("/{id}")
    fun getBookGenre(@PathVariable id: Long): Optional<BookGenre> = service.getBookGenre(id)

}
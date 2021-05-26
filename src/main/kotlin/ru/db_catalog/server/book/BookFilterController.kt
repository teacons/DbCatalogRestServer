package ru.db_catalog.server.book

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/book/filter")
class BookFilterController(
    val bookService: BookService
) {

    @GetMapping
    fun filterBook(
        @RequestParam(value = "genres", required = false) genres: Set<Long>?,
        @RequestParam(value = "authors", required = false) authors: Set<Long>?,
        @RequestParam(value = "year_down", required = true) yearDown: Int,
        @RequestParam(value = "year_up", required = true) yearUp: Int,
        @RequestParam(value = "rating_down", required = true) ratingDown: Int,
        @RequestParam(value = "rating_up", required = true) ratingUp: Int,
    ): ResponseEntity<Any> {

        val bookByYears = bookService.getBooksBetweenYears(Pair(yearDown, yearUp))

        val bookByRating = bookService.findAllByRatings(Pair(ratingDown, ratingUp))

        val bookByAuthors = if (authors != null) bookService.findAllByAuthors(authors) else null

        val bookByGenres = if (genres != null) bookService.findAllByGenres(genres) else null

        var intersected = bookByRating.intersect(bookByYears)

        if (bookByAuthors != null) intersected = intersected.intersect(bookByAuthors)

        if (bookByGenres != null) intersected = intersected.intersect(bookByGenres)

        return ResponseEntity(bookService.findBookIdNameByIds(intersected), HttpStatus.OK)
    }
}

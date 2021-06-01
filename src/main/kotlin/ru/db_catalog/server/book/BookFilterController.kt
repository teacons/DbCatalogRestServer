package ru.db_catalog.server.book

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.db_catalog.server.getSlice

@RestController
@RequestMapping("/api/book")
class BookFilterController(
    val bookService: BookService
) {

    @GetMapping("/filter")
    fun filterBook(
        @RequestParam(value = "genres", required = false) genres: Set<Long>?,
        @RequestParam(value = "authors", required = false) authors: Set<Long>?,
        @RequestParam(value = "year_down", required = true) yearDown: Int,
        @RequestParam(value = "year_up", required = true) yearUp: Int,
        @RequestParam(value = "rating_down", required = true) ratingDown: Int,
        @RequestParam(value = "rating_up", required = true) ratingUp: Int,
        @RequestParam(value = "query", required = false) searchQuery: String?,
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {

        val ids =
            bookService.filterBook(FilterBook(genres, authors, Pair(yearDown, yearUp), Pair(ratingDown, ratingUp), searchQuery))
                .sorted()

        return getSlice(id, ids, size)
    }

}

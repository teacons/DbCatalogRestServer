package ru.db_catalog.server.book

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.getSlice

@RestController
@RequestMapping("/api/book")
class BookController(
    val bookService: BookService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))

        return bookService.prepareBook(bookService.findBookById(id).get(), expanded, username)
    }

    @GetMapping("/slice")
    fun getBookSlice(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = bookService.findAllBookIds().sorted()

        return getSlice(id, ids, size)
    }

}
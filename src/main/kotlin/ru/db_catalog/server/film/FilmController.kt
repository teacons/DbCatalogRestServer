package ru.db_catalog.server.film

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.getSlice

@RestController
@RequestMapping("/api/film")
class FilmController(
    val filmService: FilmService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/{id}")
    fun getFilm(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))

        return filmService.prepareFilm(filmService.findFilmById(id).get(), expanded, username)
    }

    @GetMapping("/slice")
    fun getFilmSlice(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = filmService.findAllFilmIds().sorted()

        return getSlice(id, ids, size)
    }

}
package ru.db_catalog.server.film

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/film_genre")
class FilmGenreController(val service: FilmService) {

    @GetMapping
    fun getFilmGenres(): MutableIterable<FilmGenre> = service.findAllFilmGenres()

    @GetMapping("/{id}")
    fun getFilmGenre(@PathVariable id: Long): Optional<FilmGenre> = service.findFilmGenreById(id)

}
package ru.db_catalog.server.people

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.film.FilmService
import ru.db_catalog.server.music.MusicService

@RestController
@RequestMapping("/api/people")
class PeopleController(
    val bookService: BookService,
    val peopleService: PeopleService,
    val filmService: FilmService,
    val musicService: MusicService
) {

    @GetMapping("/book")
    fun getBookPeoples(): ResponseEntity<Any> {
        val peopleIds = bookService.findAllBookPeoples().map { it.peopleId }.toSet()

        return ResponseEntity(peopleService.findAllPeopleByIdInContentIdName(peopleIds), HttpStatus.OK)
    }

    @GetMapping("/film")
    fun getFilmPeoples(
        @RequestParam(value = "actors", required = false) actors: Boolean = false,
    ): ResponseEntity<Any> {

        val peopleIds = if (actors)
            filmService.findAllFilmPeopleByPeopleFunction(2).toSet()
        else
            filmService.findAllFilmPeopleWithoutPeopleFunction(2).toSet()

        val peoples = peopleService.findAllPeopleByIdInContentIdName(peopleIds.map { it.peopleId }.toSet())

        return ResponseEntity(peoples, HttpStatus.OK)
    }

    @GetMapping("/music")
    fun getMusicArtists(): ResponseEntity<Any> {
        val peopleIds = musicService.findAllArtists().map { it.id!! }.toSet()

        return ResponseEntity(peopleService.findAllPeopleByIdInContentIdName(peopleIds), HttpStatus.OK)
    }

    @GetMapping("/functions")
    fun getPeopleFunctions(): ResponseEntity<Any> {
        return ResponseEntity(peopleService.findAllPeopleFunction(), HttpStatus.OK)
    }

}

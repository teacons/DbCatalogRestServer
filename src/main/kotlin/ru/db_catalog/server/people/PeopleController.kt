package ru.db_catalog.server.people

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.film.FilmService
import ru.db_catalog.server.music.MusicService
import ru.db_catalog.server.user.UserService

@RestController
@RequestMapping("/api/people")
class PeopleController(
    val jwtProvider: JwtProvider,
    val userService: UserService,
    val bookService: BookService,
    val peopleService: PeopleService,
    val filmService: FilmService,
    val musicService: MusicService,
    val musicArtistService: MusicService
) {

    @GetMapping("/book")
    fun getBookPeoples(): ResponseEntity<Any> {
        val peopleIds = bookService.findAllBookPeoples().map { it.peopleId }.toSet()

        return ResponseEntity(peopleService.findAllPeopleByIdIn(peopleIds), HttpStatus.OK)
    }

    @GetMapping("/film")
    fun getFilmPeoples(): ResponseEntity<Any> {
        val peopleIds = filmService.findAllFilmPeople().toSet()

        val peoples = peopleService.findAllPeopleByIdIn(peopleIds.map { it.peopleId }.toSet())

        val answer = mutableSetOf<PeopleWithFunction>()

        for (i in peoples.indices) {
            peoples.elementAt(i).run {
                answer.add(
                    PeopleWithFunction(
                        this.id,
                        this.fullname,
                        this.yearOfBirth,
                        peopleService.findPeopleFunctionById(peopleIds.elementAt(i).peopleFunctionId).get().name
                    )
                )
            }
        }

        return ResponseEntity(answer, HttpStatus.OK)
    }

    @GetMapping("/music")
    fun getMusicArtists(): ResponseEntity<Any> {
        val peopleIds = musicService.findAllArtists().map { it.id }.toSet()

        return ResponseEntity(peopleService.findAllPeopleByIdIn(peopleIds), HttpStatus.OK)
    }

    @GetMapping("/functions")
    fun getPeopleFunctions(): ResponseEntity<Any> {
        return ResponseEntity(peopleService.findAllPeopleFunction(), HttpStatus.OK)
    }

}

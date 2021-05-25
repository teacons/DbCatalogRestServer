package ru.db_catalog.server.people

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.film.FilmService
import ru.db_catalog.server.music.Artist
import ru.db_catalog.server.music.ArtistService
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
    val peopleFunctionService: PeopleFunctionService,
    val musicService: MusicService,
    val musicArtistService: ArtistService
) {

    @GetMapping("/book")
    fun getBookPeoples(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val peopleIds = bookService.findAllBookPeoples()

        val peoples = mutableSetOf<People>()

        peopleIds.forEach{
            peoples.add(peopleService.findById(it.peopleId).get())
        }

        return ResponseEntity(peoples, HttpStatus.OK)
    }

    @GetMapping("/film")
    fun getFilmPeoples(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val peopleIds = filmService.findAllFilmPeople()

        val peoples = mutableSetOf<PeopleWithFunction>()

        peopleIds.forEach{
            val people = peopleService.findById(it.peopleId).get()
            peoples.add(PeopleWithFunction(people.id, people.fullname, people.yearOfBirth, peopleFunctionService.findById(it.peopleFunctionId).get().name))
        }

        return ResponseEntity(peoples, HttpStatus.OK)
    }

    @GetMapping("/music")
    fun getMusicArtists(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val peopleIds = musicService.findAllArtists()

        val peoples = mutableSetOf<Artist>()

        peopleIds.forEach{
            peoples.add(musicArtistService.findById(it.id).get())
        }

        return ResponseEntity(peoples, HttpStatus.OK)
    }

    @GetMapping("/functions")
    fun getPeopleFunctions(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity(peopleFunctionService.findAll(), HttpStatus.OK)
    }

}

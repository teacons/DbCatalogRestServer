package ru.db_catalog.server.film

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.Content
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.music.MusicService
import ru.db_catalog.server.people.PeopleService
import ru.db_catalog.server.people.PeopleWithFunction
import ru.db_catalog.server.top.FilmTopService
import ru.db_catalog.server.user.UserService

@RestController
@RequestMapping("/api/film")
class FilmController(
    val filmService: FilmService,
    val userService: UserService,
    val filmTopService: FilmTopService,
    val bookService: BookService,
    val musicService: MusicService,
    val peopleService: PeopleService,
    val peopleFunctionService: PeopleService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/{id}")
    fun getFilm(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val userId = userService.findByUsername(username)?.id
        return prepareFilm(filmService.findFilmById(id).get(), expanded, userId)
    }

    @GetMapping
    fun getFilms(): Set<ContentIdName> = filmService.findAllFilmsIdName()

    fun prepareFilm(film: Film, expanded: Boolean, userId: Long?): ResponseEntity<Any> {

        var rating = filmService.getFilmRating(film.id!!)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        film.filmGenres.forEach {
            genres.add(filmService.findFilmGenreById(it.filmGenreId).get().name)
        }
        if (!expanded) {
            return ResponseEntity(
                Content(
                    film.id,
                    film.name,
                    film.year,
                    film.poster,
                    rating,
                    genres
                ), HttpStatus.OK
            )
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)

            val filmSeries = if (film.filmSeriesId != null) {
                val filmSeries = filmService.findFilmSeriesById(film.filmSeriesId).get()

                ContentIdName(filmSeries.id!!, filmSeries.name)
            } else null

            val book = film.bookId?.let {
                val bookTemp = bookService.findBookById(it).get()
                ContentIdName(bookTemp.id!!, bookTemp.name)
            }

            val musics = mutableSetOf<ContentIdName>()

            film.musics.forEach {
                val tempMusic = musicService.findMusicById(it.musicId).get()
                musics.add(ContentIdName(tempMusic.id!!, tempMusic.name))
            }

            val peoples = mutableSetOf<PeopleWithFunction>()

            film.peoples.forEach {
                val people = peopleService.findPeopleById(it.peopleId).get()
                val peopleFunction = peopleFunctionService.findPeopleFunctionById(it.peopleFunctionId).get()
                peoples.add(PeopleWithFunction(people.id!!, people.fullname, people.yearOfBirth, peopleFunction.name))

            }

            val viewed = userService.existsViewByUserIdFilmId(userId, film.id)

            val userRating = userService.getUserFilmRating(userId, film.id)

            val top = filmTopService.findByFilmId(film.id).firstOrNull()
            val topIdName = top?.let { ContentIdName(it.id, it.name) }
            val topPosition = top?.let { filmTopService.findPositionInTop(it.id, film.id) }


            return ResponseEntity(
                FilmForAnswer(
                    film.id,
                    film.name,
                    film.year,
                    film.duration,
                    film.description,
                    film.poster,
                    rating,
                    filmSeries,
                    book,
                    musics,
                    peoples,
                    genres,
                    viewed,
                    userRating,
                    topIdName,
                    topPosition
                ), HttpStatus.OK
            )

        }
    }
}
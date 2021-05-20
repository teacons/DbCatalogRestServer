package ru.db_catalog.server.film

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.*
import ru.db_catalog.server.book.BookIdName
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.music.MusicIdName
import ru.db_catalog.server.music.MusicService
import ru.db_catalog.server.top.FilmTopService
import ru.db_catalog.server.top.TopIdName
import ru.db_catalog.server.user.UserService

@RestController
@RequestMapping("/api/film")
class FilmController(
    val filmService: FilmService,
    val filmGenreService: FilmGenreService,
    val userService: UserService,
    val filmTopService: FilmTopService,
    val filmSeriesService: FilmSeriesService,
    val bookService: BookService,
    val musicService: MusicService,
    val peopleService: PeopleService,
    val peopleFunctionService: PeopleFunctionService
) {

    @GetMapping("/{id}")
    fun getFilm(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestParam(value = "user_id", required = false) userId: Long?,
    ): ResponseEntity<Any> = prepareFilm(filmService.findById(id).get(), expanded, userId)

    @GetMapping
    fun getFilms(): Set<ContentIdName> = filmService.findAllIdName()

    fun prepareFilm(film: Film, expanded: Boolean, userId: Long?): ResponseEntity<Any> {

        var rating = filmService.getRating(film.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        film.filmGenres.forEach {
            genres.add(filmGenreService.getFilmGenre(it.filmGenreId).get().name)
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
                val filmSeries = filmSeriesService.findById(film.filmSeriesId).get()

                FilmSeriesIdName(filmSeries.id, filmSeries.name)
            } else null

            val book = film.book?.let {
                val bookTemp = bookService.findById(it.id).get()
                BookIdName(bookTemp.id, bookTemp.name)
            }

            val musics = mutableSetOf<MusicIdName>()

            film.musics.forEach {
                val tempMusic = musicService.findById(it.musicId).get()
                musics.add(MusicIdName(tempMusic.id, tempMusic.name))
            }

            val peoples = mutableSetOf<PeopleWithFunction>()

            film.peoples.forEach {
                val people = peopleService.findById(it.peopleId).get()
                val peopleFunction = peopleFunctionService.findById(it.peopleFunctionId).get()
                peoples.add(PeopleWithFunction(people.id, people.fullname, people.yearOfBirth, peopleFunction.name))

            }

            val viewed = userService.existsViewByUserIdFilmId(userId, film.id)

            val userRating = userService.getUserFilmRating(userId, film.id)

            val top = filmTopService.findByFilmId(film.id).firstOrNull()
            val topIdName = top?.let { TopIdName(it.id, it.name) }
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
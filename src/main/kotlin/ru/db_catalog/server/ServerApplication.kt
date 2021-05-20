package ru.db_catalog.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.book.*
import ru.db_catalog.server.film.*
import ru.db_catalog.server.music.*
import ru.db_catalog.server.top.*
import ru.db_catalog.server.user.User
import ru.db_catalog.server.user.UserService
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

@RestController
@RequestMapping("/api/music_genre")
class MusicGenreController(val service: MusicGenreService) {

    @GetMapping
    fun getMusicGenres(): MutableIterable<MusicGenre> = service.getMusicGenres()

    @GetMapping("/{id}")
    fun getMusicGenre(@PathVariable id: Long): Optional<MusicGenre> = service.getMusicGenre(id)

}

@RestController
@RequestMapping("/api/film_genre")
class FilmGenreController(val service: FilmGenreService) {

    @GetMapping
    fun getFilmGenres(): MutableIterable<FilmGenre> = service.getFilmGenres()

    @GetMapping("/{id}")
    fun getFilmGenre(@PathVariable id: Long): Optional<FilmGenre> = service.getFilmGenre(id)

}

@RestController
@RequestMapping("/api/book_genre")
class BookGenreController(val service: BookGenreService) {

    @GetMapping
    fun getBookGenres(): MutableIterable<BookGenre> = service.getBookGenres()

    @GetMapping("/{id}")
    fun getBookGenre(@PathVariable id: Long): Optional<BookGenre> = service.getBookGenre(id)

}

@RestController
@RequestMapping("/api/book")
class BookController(
    val bookService: BookService,
    val bookGenreService: BookGenreService,
    val peopleService: PeopleService,
    val bookSeriesService: BookSeriesService,
    val bookTopService: BookTopService,
    val userService: UserService
) {

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestParam(value = "user_id", required = false) userId: Long?,
        response: HttpServletResponse
    ): ResponseEntity<Any> {
        return prepareBook(bookService.findById(id).get(), expanded, userId)
    }

    @GetMapping
    fun getBooks(): Set<ContentIdName> = bookService.findAllIdName()

    fun prepareBook(book: Book, expanded: Boolean, userId: Long?): ResponseEntity<Any> {

        var rating = bookService.getRating(book.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        book.bookGenres.forEach {
            genres.add(bookGenreService.getBookGenre(it.bookGenreId).get().name)
        }

        if (!expanded) {
            return ResponseEntity(Content(book.id, book.name, book.year, book.poster, rating, genres), HttpStatus.OK)
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)
            val bookSeriesWithoutBooks = if (book.bookSeriesId != null) {
                val bookSeries = bookSeriesService.findById(book.bookSeriesId).get()

                BookSeriesIdName(bookSeries.id, bookSeries.name)
            } else null

            val authors = mutableSetOf<People>()

            book.authors.forEach {
                authors.add(peopleService.findById(it.peopleId).get())
            }

            val viewed = userService.existsViewByUserIdBookId(userId, book.id)

            val userRating = userService.getUserBookRating(userId, book.id)

            val top = bookTopService.findByBookId(book.id).firstOrNull()
            val topIdName = top?.let { TopIdName(it.id, it.name) }
            val topPosition = top?.let { bookTopService.findPositionInTop(it.id, book.id) }

            return ResponseEntity(
                BookForAnswer(
                    book.id,
                    book.name,
                    book.year,
                    book.description,
                    book.poster,
                    rating,
                    bookSeriesWithoutBooks,
                    authors,
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


@RestController
@RequestMapping("/api/music")
class MusicController(
    val musicService: MusicService,
    val musicGenreService: MusicGenreService,
    val musicAlbumService: MusicAlbumService,
    val artistService: ArtistService,
    val userService: UserService,
    val musicTopService: MusicTopService

) {

    @GetMapping("/{id}")
    fun getMusic(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestParam(value = "user_id", required = false) userId: Long?,
    ): ResponseEntity<Any> = prepareMusic(musicService.findById(id).get(), expanded, userId)


    @GetMapping
    fun getMusics(): Set<ContentIdName> = musicService.findAllIdName()

    fun prepareMusic(music: Music, expanded: Boolean, userId: Long?): ResponseEntity<Any> {

        var rating = musicService.getRating(music.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        music.musicGenres.forEach {
            genres.add(musicGenreService.getMusicGenre(it.musicGenreId).get().name)
        }

        val poster = music.albums.firstOrNull()?.let { musicAlbumService.findById(it.musicAlbumId).get().poster }
        if (!expanded) {
            return ResponseEntity(Content(music.id, music.name, music.year, poster, rating, genres), HttpStatus.OK)
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)

            val albums = mutableSetOf<MusicAlbum>()

            music.albums.forEach {
                albums.add(musicAlbumService.findById(it.musicAlbumId).get())
            }

            val artists = mutableSetOf<Artist>()

            music.artists.forEach {
                artists.add(artistService.findById(it.artistId).get())
            }

            val viewed = userService.existsViewByUserIdMusicId(userId, music.id)

            val userRating = userService.getUserMusicRating(userId, music.id)

            val top = musicTopService.findByMusicId(music.id).firstOrNull()
            val topIdName = top?.let { TopIdName(it.id, it.name) }
            val topPosition = top?.let { musicTopService.findPositionInTop(it.id, music.id) }

            return ResponseEntity(
                MusicForAnswer(
                    music.id,
                    music.name,
                    music.year,
                    music.duration,
                    poster,
                    rating,
                    artists,
                    albums,
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


@RestController
@RequestMapping("/api/top")
class TopController(
    val bookTopService: BookTopService,
    val musicTopService: MusicTopService,
    val filmTopService: FilmTopService
) {

    @GetMapping("/book")
    fun getBookTops(): Set<TopIdName> {
        return bookTopService.findAllIdName()

    }

    @GetMapping("/book/{id}")
    fun getBookTop(@PathVariable id: Long): Optional<BookTop> {
        return bookTopService.findById(id)
    }

    @GetMapping("/music")
    fun getMusicTops(): Set<TopIdName> {
        return musicTopService.findAllIdName()

    }

    @GetMapping("/music/{id}")
    fun getMusicTop(@PathVariable id: Long): Optional<MusicTop> {
        return musicTopService.findById(id)
    }

    @GetMapping("/film")
    fun getFilmTops(): Set<TopIdName> {
        return filmTopService.findAllIdName()

    }

    @GetMapping("/film/{id}")
    fun getFilmTop(@PathVariable id: Long): Optional<FilmTop> {
        return filmTopService.findById(id)
    }

}

@RestController
@RequestMapping("/api/user")
class UserController(val userService: UserService) {

    @GetMapping("/auth")
    fun authUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String
    ): Map<String, Long?> {
        val user = userService.findByUsername(username)
        return if (user != null && user.password == password) mapOf(Pair("uid", user.id))
        else mapOf(Pair("uid", null))
    }

    @PostMapping("/reg")
    fun registerUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String,
        @RequestParam(value = "email", required = true)
        email: String
    ): Map<String, String?> {
        val answer = mutableMapOf<String, String?>()
        when {
            userService.existsUserByUsername(username) -> {
                answer["Code"] = "1"
                answer["UserId"] = null
                return answer
            }
            username.length > 20 -> {
                answer["Code"] = "2"
                answer["UserId"] = null
                return answer
            }
            userService.existsUserByEmail(email) -> {
                answer["Code"] = "3"
                answer["UserId"] = null
                return answer
            }
            password.length > 32 -> {
                answer["Code"] = "4"
                answer["UserId"] = null
                return answer
            }
            password.length < 6 -> {
                answer["Code"] = "5"
                answer["UserId"] = null
                return answer
            }
            else -> {
                return try {
                    val user = User(null, username, password, email, Timestamp(Calendar.getInstance().timeInMillis))
                    val savedUser = userService.save(user)
                    answer["Code"] = "0"
                    answer["UserId"] = savedUser.id.toString()
                    answer
                } catch (e: Exception) {
                    answer["Code"] = "666"
                    answer["UserId"] = null
                    answer
                }
            }
        }
    }

}



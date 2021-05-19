package ru.db_catalog.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.book.Book
import ru.db_catalog.server.book.BookGenre
import ru.db_catalog.server.book.Content
import ru.db_catalog.server.book.ContentSimple
import ru.db_catalog.server.film.Film
import ru.db_catalog.server.music.Music
import ru.db_catalog.server.music.MusicGenre
import ru.db_catalog.server.top.BookTop
import ru.db_catalog.server.user.User
import java.sql.Timestamp
import java.util.*

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
    val peopleService: PeopleService
) {

    @GetMapping("/{id}")
    fun getBook(@PathVariable id: Long): Content {
        return prepareBook(bookService.findById(id).get())
    }

    @GetMapping
    fun getBooks(): Set<ContentSimple> = bookService.findAllIdName()

    fun prepareBook(book: Book): Content {
//        val bookSeriesWithoutBooks = if (book.bookSeriesId != null) {
//            val bookSeries = bookSeriesService.findById(book.bookSeriesId).get()
//
//            BookSeriesWithoutBooks(bookSeries.id, bookSeries.name, bookSeries.description)
//        } else null

        var rating = bookService.getRating(book.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        book.bookGenres.forEach {
            genres.add(bookGenreService.getBookGenre(it.bookGenreId).get().name)
        }

//        val authors = mutableSetOf<People>()

//        book.authors.forEach {
//            authors.add(peopleService.findById(it.peopleId).get())
//        }

//        val tops = mutableSetOf<BookTopWithoutBooks>()
//
//        bookTopService.findByBookId(book.id).forEach {
//            tops.add(BookTopWithoutBooks(it.id, it.name, bookTopService.findPositionInTop(it.id, book.id)))
//        }


        return Content(
            book.id,
            book.name,
            book.year,
            book.poster,
            rating,
            genres,
        )
    }

}


@RestController
@RequestMapping("/api/music")
class MusicController(
    val musicService: MusicService,
    val musicGenreService: MusicGenreService,
    val musicAlbumService: MusicAlbumService
) {

    @GetMapping("/{id}")
    fun getMusic(@PathVariable id: Long): Content = prepareMusic(musicService.findById(id).get())


    @GetMapping
    fun getMusics(): Set<ContentSimple> = musicService.findAllIdName()

    fun prepareMusic(music: Music): Content {

        var rating = musicService.getRating(music.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        music.musicGenres.forEach {
            genres.add(musicGenreService.getMusicGenre(it.musicGenreId).get().name)
        }

        val poster = music.albums.firstOrNull()?.let { musicAlbumService.findById(it.musicAlbumId).get().poster }

        return Content(
            music.id,
            music.name,
            music.year,
            poster,
            rating,
            genres,
        )
    }
}

@RestController
@RequestMapping("/api/film")
class FilmController(val filmService: FilmService, val filmGenreService: FilmGenreService) {

    @GetMapping("/{id}")
    fun getFilm(@PathVariable id: Long): Content = prepareFilm(filmService.findById(id).get())

    @GetMapping
    fun getFilms(): Set<ContentSimple> = filmService.findAllIdName()

    fun prepareFilm(film: Film): Content {

        var rating = filmService.getRating(film.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        film.filmGenres.forEach {
            genres.add(filmGenreService.getFilmGenre(it.filmGenreId).get().name)
        }

        return Content(
            film.id,
            film.name,
            film.year,
            film.poster,
            rating,
            genres,
        )
    }
}


@RestController
@RequestMapping("/api/top")
class TopController(val bookTopService: BookTopService) {

    @GetMapping("/book")
    fun getBookTops(): MutableIterable<BookTop> {
        return bookTopService.findAll()

    }

    @GetMapping("/book/{id}")
    fun getBookTop(@PathVariable id: Long): Optional<BookTop> {
        return bookTopService.findById(id)
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



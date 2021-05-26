package ru.db_catalog.server.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.film.FilmService
import ru.db_catalog.server.music.MusicService
import java.sql.Timestamp
import java.util.*


@RestController
@RequestMapping("/api/user/update")
class UserUpdateMediaController(
    val userService: UserService,
    val jwtProvider: JwtProvider,
    val bookGenreService: BookService,
    val filmGenreService: FilmService,
    val musicGenreService: MusicService
) {
    @PostMapping("/{type}/{id}")
    fun updateViewed(
        @PathVariable id: Long,
        @PathVariable type: String,
        @RequestHeader("Authorization") token: String,
        @RequestParam("viewed", required = true) viewed: Boolean,
        @RequestParam("rating", required = false) rating: Int?
    ): ResponseEntity<HttpStatus> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        when (type) {
            "book" -> updateViewedBook(user, id, viewed, rating)
            "film" -> updateViewedFilm(user, id, viewed, rating)
            "music" -> updateViewedMusic(user, id, viewed, rating)
            else -> return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(HttpStatus.OK)


    }

    private fun updateViewedMusic(user: User, id: Long, viewed: Boolean, rating: Int?) {
        user.viewedMusic.find { it.musicId == id }.run {
            if (this == null) {
                if (viewed)
                    user.viewedMusic.add(
                        UserViewedMusicRef(id, rating, Timestamp(Calendar.getInstance().timeInMillis))
                    )
            } else
                if (viewed) this.rating = rating
                else user.viewedMusic.remove(this)
            userService.save(user)
        }
    }

    private fun updateViewedFilm(user: User, id: Long, viewed: Boolean, rating: Int?) {
        user.viewedFilm.find { it.filmId == id }.run {
            if (this == null) {
                if (viewed)
                    user.viewedFilm.add(
                        UserViewedFilmRef(id, rating, Timestamp(Calendar.getInstance().timeInMillis))
                    )
            } else
                if (viewed) this.rating = rating
                else user.viewedFilm.remove(this)
            userService.save(user)
        }
    }

    private fun updateViewedBook(user: User, id: Long, viewed: Boolean, rating: Int?) {
        user.viewedBook.find { it.bookId == id }.run {
            if (this == null) {
                if (viewed)
                    user.viewedBook.add(
                        UserViewedBookRef(id, rating, Timestamp(Calendar.getInstance().timeInMillis))
                    )
            } else
                if (viewed) this.rating = rating
                else user.viewedBook.remove(this)
            userService.save(user)
        }
    }

    @PostMapping("/genre")
    fun updateGenre(
        @RequestHeader("Authorization") token: String,
        @RequestParam("book_genres") book_genres: Set<Long>,
        @RequestParam("film_genres") film_genres: Set<Long>,
        @RequestParam("music_genres") music_genres: Set<Long>,
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        user.likedBookGenres.clear()
        book_genres.forEach {
            if (bookGenreService.existsBookGenreById(it)) user.likedBookGenres.add(UserBookGenreRef(it))
            user.likedFilmGenres.clear()
        }
        film_genres.forEach {
            if (filmGenreService.existsFilmGenreById(it)) user.likedFilmGenres.add(UserFilmGenreRef(it))
        }
        user.likedMusicGenres.clear()
        music_genres.forEach {
            if (musicGenreService.existsMusicGenreById(it)) user.likedMusicGenres.add(UserMusicGenreRef(it))
        }

        userService.save(user)

        return ResponseEntity(HttpStatus.OK)
    }
}
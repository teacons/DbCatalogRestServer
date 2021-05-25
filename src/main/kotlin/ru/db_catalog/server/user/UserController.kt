package ru.db_catalog.server.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.AuthResponse
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.film.FilmService
import ru.db_catalog.server.music.MusicService
import java.sql.Timestamp
import java.util.*


@RestController
@RequestMapping("/api/user")
class UserController(
    val userService: UserService,
    val jwtProvider: JwtProvider,
    val bookGenreService: BookService,
    val filmGenreService: FilmService,
    val musicGenreService: MusicService
) {

    @GetMapping("/auth")
    fun authUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String
    ): AuthResponse {
        val user = userService.findByUsernameAndPassword(username, password)
        val token = user?.let { jwtProvider.generateToken(it.username) }
        return AuthResponse(token)
    }

    @PostMapping("/reg")
    fun registerUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String,
        @RequestParam(value = "email", required = true)
        email: String
    ): UserRegisterAnswerAndChange {
        when {
            userService.existsUserByUsername(username) -> {
                return UserRegisterAnswerAndChange(1)
            }
            username.length > 20 -> {
                return UserRegisterAnswerAndChange(2)
            }
            userService.existsUserByEmail(email) -> {
                return UserRegisterAnswerAndChange(3)
            }
            password.length > 32 -> {
                return UserRegisterAnswerAndChange(4)
            }
            password.length < 6 -> {
                return UserRegisterAnswerAndChange(5)
            }
            else -> {
                return try {
                    val user = User(null, username, password, email, Timestamp(Calendar.getInstance().timeInMillis), 1)
                    userService.saveWithEncrypt(user)
                    UserRegisterAnswerAndChange(0)
                } catch (e: Exception) {
                    UserRegisterAnswerAndChange(666)
                }
            }
        }
    }

    @PostMapping("/update/book/{id}")
    fun updateViewedBook(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
        @RequestParam("viewed", required = true) viewed: Boolean,
        @RequestParam("rating", required = false) rating: Int?
    ): ResponseEntity<HttpStatus> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        user.viewedBook.find { it.bookId == id }.run {
            if (this == null) {
                if (viewed)
                    user.viewedBook.add(UserViewedBookRef(id, rating, Timestamp(Calendar.getInstance().timeInMillis)))
            } else
                if (viewed) this.rating = rating
                else user.viewedBook.remove(this)
            userService.save(user)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/update/film/{id}")
    fun updateViewedFilm(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
        @RequestParam("viewed", required = true) viewed: Boolean,
        @RequestParam("rating", required = false) rating: Int?
    ): ResponseEntity<HttpStatus> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        user.viewedFilm.find { it.filmId == id }.run {
            if (this == null) {
                if (viewed)
                    user.viewedFilm.add(UserViewedFilmRef(id, rating, Timestamp(Calendar.getInstance().timeInMillis)))
            } else
                if (viewed) this.rating = rating
                else user.viewedFilm.remove(this)
            userService.save(user)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/update/music/{id}")
    fun updateViewedMusic(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
        @RequestParam("viewed", required = true) viewed: Boolean,
        @RequestParam("rating", required = false) rating: Int?
    ): ResponseEntity<HttpStatus> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        user.viewedMusic.find { it.musicId == id }.run {
            if (this == null) {
                if (viewed)
                    user.viewedMusic.add(UserViewedMusicRef(id, rating, Timestamp(Calendar.getInstance().timeInMillis)))
            } else
                if (viewed) this.rating = rating
                else user.viewedMusic.remove(this)
            userService.save(user)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/info")
    fun getUserInfo(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val userForAnswer = UserForAnswer(user.username, user.email, user.createTime)
        return ResponseEntity(userForAnswer, HttpStatus.OK)
    }

    @PostMapping("/update/username")
    fun updateUsername(
        @RequestHeader("Authorization") token: String,
        @RequestParam("new_username") newUserName: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        when {
            userService.existsUserByUsername(newUserName) -> {
                return ResponseEntity(UserRegisterAnswerAndChange(1), HttpStatus.BAD_REQUEST)
            }
            newUserName.length > 20 -> {
                return ResponseEntity(UserRegisterAnswerAndChange(2), HttpStatus.BAD_REQUEST)
            }
            else -> {
                user.username = newUserName
                userService.save(user)
            }
        }
        return ResponseEntity(UserRegisterAnswerAndChange(0), HttpStatus.OK)
    }

    @PostMapping("/update/password")
    fun updatePassword(
        @RequestHeader("Authorization") token: String,
        @RequestParam("old_password") oldPassword: String,
        @RequestParam("new_password") newPassword: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsernameAndPassword(username, oldPassword) ?: return ResponseEntity(
            UserRegisterAnswerAndChange(6),
            HttpStatus.BAD_REQUEST
        )


        when {
            newPassword.length > 32 -> {
                return ResponseEntity(UserRegisterAnswerAndChange(4), HttpStatus.BAD_REQUEST)
            }
            newPassword.length < 6 -> {
                return ResponseEntity(UserRegisterAnswerAndChange(5), HttpStatus.BAD_REQUEST)
            }
            else -> {
                user.password = newPassword
                userService.saveWithEncrypt(user)
            }
        }
        return ResponseEntity(UserRegisterAnswerAndChange(0), HttpStatus.OK)
    }

    @PostMapping("/update/email")
    fun updateEmail(
        @RequestHeader("Authorization") token: String,
        @RequestParam("new_email") newEmail: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        when {
            userService.existsUserByEmail(newEmail) -> {
                return ResponseEntity(UserRegisterAnswerAndChange(3), HttpStatus.BAD_REQUEST)
            }
            else -> {
                user.email = newEmail
                userService.save(user)
            }
        }
        return ResponseEntity(UserRegisterAnswerAndChange(0), HttpStatus.OK)
    }

    @PostMapping("/update/genre/book")
    fun updateBookGenre(
        @RequestHeader("Authorization") token: String,
        @RequestParam("genres") genres: Set<Long>
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        user.likedBookGenres.clear()

        genres.forEach {
            if (bookGenreService.existsBookGenreById(it)) user.likedBookGenres.add(UserBookGenreRef(it))
        }

        userService.save(user)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/update/genre/film")
    fun updateFilmGenre(
        @RequestHeader("Authorization") token: String,
        @RequestParam("genres") genres: Set<Long>
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        user.likedFilmGenres.clear()

        genres.forEach {
            if (filmGenreService.existsFilmGenreById(it)) user.likedFilmGenres.add(UserFilmGenreRef(it))
        }

        userService.save(user)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/update/genre/music")
    fun updateMusicGenre(
        @RequestHeader("Authorization") token: String,
        @RequestParam("genres") genres: Set<Long>
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        user.likedMusicGenres.clear()

        genres.forEach {
            if (musicGenreService.existsMusicGenreById(it)) user.likedMusicGenres.add(UserMusicGenreRef(it))
        }

        userService.save(user)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/genre/book")
    fun getBookGenres(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity(getGenres(user, GenreType.BOOK), HttpStatus.OK)
    }

    @GetMapping("/genre/film")
    fun getFilmGenres(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity(getGenres(user, GenreType.FILM), HttpStatus.OK)
    }

    @GetMapping("/genre/music")
    fun getMusicGenres(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity(getGenres(user, GenreType.MUSIC), HttpStatus.OK)
    }

    fun getGenres(user: User, type: GenreType): Set<Long> {
        val genres = mutableSetOf<Long>()

        when (type) {
            GenreType.BOOK ->
                user.likedBookGenres.forEach { genres.add(it.bookGenreId) }

            GenreType.FILM ->
                user.likedFilmGenres.forEach { genres.add(it.filmGenreId) }

            GenreType.MUSIC ->
                user.likedMusicGenres.forEach { genres.add(it.musicGenreId) }
        }

        return genres
    }

    @GetMapping("/book")
    fun getViewedBooks(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val setOfViewed = user.viewedBook.map { it.bookId }.toSet()
        return if (setOfViewed.isEmpty()) ResponseEntity(HttpStatus.OK)
        else ResponseEntity(userService.getBookNamesByIds(setOfViewed), HttpStatus.OK)
    }

    @GetMapping("/film")
    fun getViewedFilm(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val setOfViewed = user.viewedFilm.map { it.filmId }.toSet()
        return if (setOfViewed.isEmpty()) ResponseEntity(HttpStatus.OK)
        else ResponseEntity(userService.getFilmNamesByIds(setOfViewed), HttpStatus.OK)
    }

    @GetMapping("/music")
    fun getViewedMusics(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val setOfViewed = user.viewedMusic.map { it.musicId }.toSet()
        return if (setOfViewed.isEmpty()) ResponseEntity(HttpStatus.OK)
        else ResponseEntity(userService.getMusicNamesByIds(setOfViewed), HttpStatus.OK)
    }

    @GetMapping("/recommended/{type}")
    fun getRecommended(
        @RequestHeader("Authorization") token: String,
        @PathVariable type: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        when (type) {
            "book" -> {
                val genres = user.likedBookGenres.map { it.bookGenreId }.toSet()
                if (genres.isEmpty()) return ResponseEntity(HttpStatus.OK)
                return ResponseEntity(userService.getUserRecommendedBook(genres), HttpStatus.OK)
            }
            "film" -> {
                val genres = user.likedFilmGenres.map { it.filmGenreId }.toSet()
                if (genres.isEmpty()) return ResponseEntity(HttpStatus.OK)
                return ResponseEntity(userService.getUserRecommendedFilm(genres), HttpStatus.OK)
            }
            "music" -> {
                val genres = user.likedMusicGenres.map { it.musicGenreId }.toSet()
                if (genres.isEmpty()) return ResponseEntity(HttpStatus.OK)
                return ResponseEntity(userService.getUserRecommendedMusic(genres), HttpStatus.OK)
            }
            else -> return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

}

enum class GenreType {
    BOOK, FILM, MUSIC
}


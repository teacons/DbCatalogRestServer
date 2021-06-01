package ru.db_catalog.server.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.ContentType
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.getSlice

@RestController
@RequestMapping("/api/user")
class UserMediaGetterController(
    val userService: UserService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/genre/{type}")
    fun getGenres(
        @RequestHeader("Authorization") token: String,
        @PathVariable type: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val genreType = when (type) {
            "book" -> ContentType.BOOK
            "film" -> ContentType.FILM
            "music" -> ContentType.MUSIC
            else -> return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(getGenres(user, genreType), HttpStatus.OK)
    }

    fun getGenres(user: User, type: ContentType): Set<Long> {
        return when (type) {
            ContentType.BOOK -> user.likedBookGenres.map { it.bookGenreId }
            ContentType.FILM -> user.likedFilmGenres.map { it.filmGenreId }
            ContentType.MUSIC -> user.likedMusicGenres.map { it.musicGenreId }
        }.toSet()
    }

    @GetMapping("/{type}")
    fun getViewed(
        @RequestHeader("Authorization") token: String,
        @PathVariable type: String,
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): ResponseEntity<List<Long>> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val ids = when (type) {
            "book" -> {
                user.viewedBook.map { it.bookId }.sorted()

            }
            "film" -> {
                user.viewedFilm.map { it.filmId }.sorted()
            }
            "music" -> {
                user.viewedMusic.map { it.musicId }.sorted()
            }
            else -> throw IllegalArgumentException("getViewed: bad type")
        }

        return ResponseEntity(getSlice(id, ids, size), HttpStatus.OK)
    }

    @GetMapping("/recommended/{type}")
    fun getRecommended(
        @RequestHeader("Authorization") token: String,
        @PathVariable type: String,
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): ResponseEntity<List<Long>> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val ids = when (type) {
            "book" -> {
                val genres = user.likedBookGenres.map { it.bookGenreId }.toSet()
                val viewed = user.viewedBook.map { it.bookId }.toSet()
                userService.getUserRecommendedBook(genres, viewed).sorted()
            }
            "film" -> {
                val genres = user.likedFilmGenres.map { it.filmGenreId }.toSet()
                val viewed = user.viewedFilm.map { it.filmId }.toSet()
                userService.getUserRecommendedFilm(genres, viewed).sorted()
            }
            "music" -> {
                val genres = user.likedMusicGenres.map { it.musicGenreId }.toSet()
                val viewed = user.viewedMusic.map { it.musicId }.toSet()
                userService.getUserRecommendedMusic(genres, viewed).sorted()
            }
            else -> throw IllegalArgumentException("getRecommended: bad type")
        }

        return ResponseEntity(getSlice(id, ids, size), HttpStatus.OK)
    }
}
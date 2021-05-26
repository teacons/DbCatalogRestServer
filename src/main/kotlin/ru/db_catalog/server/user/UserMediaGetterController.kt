package ru.db_catalog.server.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.ContentType
import ru.db_catalog.server.JwtProvider

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
        @PathVariable type: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        when (type) {
            "book" -> {
                val setOfViewed = user.viewedBook.map { it.bookId }.toSet()
                return if (setOfViewed.isEmpty()) ResponseEntity(HttpStatus.OK)
                else ResponseEntity(userService.getBookNamesByIds(setOfViewed), HttpStatus.OK)
            }
            "film" -> {
                val setOfViewed = user.viewedFilm.map { it.filmId }.toSet()
                return if (setOfViewed.isEmpty()) ResponseEntity(HttpStatus.OK)
                else ResponseEntity(userService.getFilmNamesByIds(setOfViewed), HttpStatus.OK)
            }
            "music" -> {
                val setOfViewed = user.viewedMusic.map { it.musicId }.toSet()
                return if (setOfViewed.isEmpty()) ResponseEntity(HttpStatus.OK)
                else ResponseEntity(userService.getMusicNamesByIds(setOfViewed), HttpStatus.OK)
            }
            else -> return ResponseEntity(HttpStatus.BAD_REQUEST)
        }


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
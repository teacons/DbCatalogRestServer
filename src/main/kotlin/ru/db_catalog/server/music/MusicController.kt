package ru.db_catalog.server.music

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.Content
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.top.MusicTopService
import ru.db_catalog.server.user.UserService

@RestController
@RequestMapping("/api/music")
class MusicController(
    val musicService: MusicService,
    val userService: UserService,
    val musicTopService: MusicTopService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/{id}")
    fun getMusic(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val userId = userService.findByUsername(username)?.id
        return prepareMusic(musicService.findMusicById(id).get(), expanded, userId)
    }


    @GetMapping
    fun getMusics(): Set<ContentIdName> = musicService.findAllMusicIdName()

    fun prepareMusic(music: Music, expanded: Boolean, userId: Long?): ResponseEntity<Any> {

        var rating = musicService.getMusicRating(music.id)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        music.musicGenres.forEach {
            genres.add(musicService.findMusicGenreById(it.musicGenreId).get().name)
        }

        val poster = music.albums.firstOrNull()?.let { this.musicService.findMusicAlbumById(it.musicAlbumId).get().poster }
        if (!expanded) {
            return ResponseEntity(Content(music.id, music.name, music.year, poster, rating, genres), HttpStatus.OK)
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)

            val albums = mutableSetOf<MusicAlbum>()

            music.albums.forEach {
                albums.add(this.musicService.findMusicAlbumById(it.musicAlbumId).get())
            }

            val artists = mutableSetOf<Artist>()

            music.artists.forEach {
                artists.add(musicService.findArtistById(it.artistId).get())
            }

            val viewed = userService.existsViewByUserIdMusicId(userId, music.id)

            val userRating = userService.getUserMusicRating(userId, music.id)

            val top = musicTopService.findByMusicId(music.id).firstOrNull()
            val topIdName = top?.let { ContentIdName(it.id, it.name) }
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
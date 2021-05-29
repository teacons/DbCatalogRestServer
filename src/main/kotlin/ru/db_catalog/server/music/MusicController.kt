package ru.db_catalog.server.music

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.getSlice

@RestController
@RequestMapping("/api/music")
class MusicController(
    val musicService: MusicService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/{id}")
    fun getMusic(
        @PathVariable id: Long,
        @RequestParam(value = "expanded", required = false) expanded: Boolean = false,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))

        return musicService.prepareMusic(musicService.findMusicById(id).get(), expanded, username)
    }


    @GetMapping("/slice")
    fun getMusicSlice(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = musicService.findAllMusicIds().sorted()

        return getSlice(id, ids, size)
    }

}
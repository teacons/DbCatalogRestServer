package ru.db_catalog.server.music

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/music_genre")
class MusicGenreController(val service: MusicGenreService) {

    @GetMapping
    fun getMusicGenres(): MutableIterable<MusicGenre> = service.getMusicGenres()

    @GetMapping("/{id}")
    fun getMusicGenre(@PathVariable id: Long): Optional<MusicGenre> = service.getMusicGenre(id)

}
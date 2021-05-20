package ru.db_catalog.server.top

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

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
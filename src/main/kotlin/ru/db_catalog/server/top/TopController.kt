package ru.db_catalog.server.top

import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.getSlice
import java.util.*

@RestController
@RequestMapping("/api/top")
class TopController(
    val bookTopService: BookTopService,
    val musicTopService: MusicTopService,
    val filmTopService: FilmTopService
) {

    @GetMapping("/book")
    fun getBookTops(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = bookTopService.findAllId().sorted()
        return getSlice(id, ids, size)

    }

    @GetMapping("/book/{id}")
    fun getBookTop(@PathVariable id: Long): Optional<BookTop> {
        return bookTopService.findById(id)
    }

    @GetMapping("/music")
    fun getMusicTops(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = musicTopService.findAllId().sorted()
        return getSlice(id, ids, size)
    }

    @GetMapping("/music/{id}")
    fun getMusicTop(@PathVariable id: Long): Optional<MusicTop> {
        return musicTopService.findById(id)
    }

    @GetMapping("/film")
    fun getFilmTops(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = filmTopService.findAllId().sorted()
        return getSlice(id, ids, size)
    }

    @GetMapping("/film/{id}")
    fun getFilmTop(@PathVariable id: Long): Optional<FilmTop> {
        return filmTopService.findById(id)
    }

    @GetMapping("/{type}/search")
    fun searchTop(
        @PathVariable type: String,
        @RequestParam(value = "query", required = true) query: String,
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = when (type) {
            "book" -> bookTopService.findAllMusicsByName("%${query.toLowerCase()}%")
            "film" -> filmTopService.findAllMusicsByName("%${query.toLowerCase()}%")
            "music" -> musicTopService.findAllMusicsByName("%${query.toLowerCase()}%")
            else -> throw IllegalArgumentException("searchTop: bad type")
        }.sorted()
         return getSlice(id, ids, size)
    }

}
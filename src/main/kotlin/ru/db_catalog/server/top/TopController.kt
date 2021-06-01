package ru.db_catalog.server.top

import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.ContentIdName
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
    ): List<ContentIdName> {
        val ids = bookTopService.findAllIdName().sortedBy { it.id }
        return getSlice(if (id == null) null else ids.find { it.id == id }, ids, size)
    }

    @GetMapping("/book/{id}")
    fun getBookTop(@PathVariable id: Long): Optional<BookTop> {
        return bookTopService.findById(id)
    }

    @GetMapping("/music")
    fun getMusicTops(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<ContentIdName> {
        val ids = musicTopService.findAllId().sortedBy { it.id }
        return getSlice(if (id == null) null else ids.find { it.id == id }, ids, size)
    }

    @GetMapping("/music/{id}")
    fun getMusicTop(@PathVariable id: Long): Optional<MusicTop> {
        return musicTopService.findById(id)
    }

    @GetMapping("/film")
    fun getFilmTops(
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<ContentIdName> {
        val ids = filmTopService.findAllIdName().sortedBy { it.id }
        return getSlice(if (id == null) null else ids.find { it.id == id }, ids, size)
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
    ): List<ContentIdName> {
        val ids = when (type) {
            "book" -> bookTopService.findAllBooksIdNameByName("%${query.toLowerCase()}%")
            "film" -> filmTopService.findAllFilmsIdNameByName("%${query.toLowerCase()}%")
            "music" -> musicTopService.findAllMusicsIdNameByName("%${query.toLowerCase()}%")
            else -> throw IllegalArgumentException("searchTop: bad type")
        }.sortedBy { it.id }
         return getSlice(if (id == null) null else ids.find { it.id == id }, ids, size)
    }

}

fun getSlice(
    element: ContentIdName?,
    ids: List<ContentIdName>,
    size: Int
): List<ContentIdName> {
    return if (element != null) {
        val fromId = ids.indexOf(element)
        val toId = if (fromId + 1 + size <= ids.size) fromId + 1 + size else ids.size
        ids.subList(fromId + 1, toId)
    } else {
        val toId = if (size <= ids.size) size else ids.size
        ids.subList(0, toId)
    }
}
package ru.db_catalog.server.music

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.db_catalog.server.getSlice

@RestController
@RequestMapping("/api/music")
class MusicFilterController(
    val musicService: MusicService
) {

    @GetMapping("/filter")
    fun filterMusic(
        @RequestParam(value = "genres", required = false) genres: Set<Long>?,
        @RequestParam(value = "artists", required = false) artists: Set<Long>?,
        @RequestParam(value = "duration_down", required = true) durationDown: Int,
        @RequestParam(value = "duration_up", required = true) durationUp: Int,
        @RequestParam(value = "year_down", required = true) yearDown: Int,
        @RequestParam(value = "year_up", required = true) yearUp: Int,
        @RequestParam(value = "rating_down", required = true) ratingDown: Int,
        @RequestParam(value = "rating_up", required = true) ratingUp: Int,
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {

        val ids = musicService.filterMusic(
            MusicFilter(
                genres,
                artists,
                Pair(durationDown, durationUp),
                Pair(yearDown, yearUp),
                Pair(ratingDown, ratingUp)
            )
        ).sorted()

        return getSlice(id, ids, size)
    }

    @GetMapping("/search")
    fun searchMusic(
        @RequestParam(value = "query", required = true) query: String,
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {
        val ids = musicService.findAllMusicsByName("%${query.toLowerCase()}%").sorted()

        return getSlice(id, ids, size)
    }

}
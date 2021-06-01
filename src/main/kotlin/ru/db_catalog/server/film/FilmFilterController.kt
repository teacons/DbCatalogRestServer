package ru.db_catalog.server.film

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.db_catalog.server.getSlice


@RestController
@RequestMapping("/api/film")
class FilmFilterController(
    val filmService: FilmService
) {
    @GetMapping("/filter")
    fun filterFilm(
        @RequestParam(value = "genres", required = false) genres: Set<Long>?,
        @RequestParam(value = "actors", required = false) actors: Set<Long>?,
        @RequestParam(value = "creators", required = false) creators: Set<Long>?,
        @RequestParam(value = "duration_down", required = true) durationDown: Int,
        @RequestParam(value = "duration_up", required = true) durationUp: Int,
        @RequestParam(value = "year_down", required = true) yearDown: Int,
        @RequestParam(value = "year_up", required = true) yearUp: Int,
        @RequestParam(value = "rating_down", required = true) ratingDown: Int,
        @RequestParam(value = "rating_up", required = true) ratingUp: Int,
        @RequestParam(value = "query", required = false) searchQuery: String?,
        @RequestParam(value = "id", required = false) id: Long?,
        @RequestParam(value = "size", required = true) size: Int
    ): List<Long> {

        val ids = filmService.filterFilm(
            FilterFilm(
                genres,
                actors,
                creators,
                Pair(durationDown, durationUp),
                Pair(yearDown, yearUp),
                Pair(ratingDown, ratingUp),
                searchQuery
            )
        ).sorted()

        return getSlice(id, ids, size)
    }

}
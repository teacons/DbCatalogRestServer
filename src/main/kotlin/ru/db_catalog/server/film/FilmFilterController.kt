package ru.db_catalog.server.film

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/film/filter")
class FilmFilterController(
    val filmService: FilmService
) {
    @GetMapping
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
    ): ResponseEntity<Any> {

        val filmByYears = filmService.getFilmsBetweenYears(Pair(yearDown, yearUp))

        val filmByRating = filmService.findFilmsByRatings(Pair(ratingDown, ratingUp))

        val filmByDuration = filmService.findAllByDuration(durationDown, durationUp)

        val filmByActors = if (actors != null) filmService.findAllByActors(actors) else null

        val filmByCreators = if (creators != null) filmService.findAllByCreators(creators) else null

        val filmByGenres = if (genres != null) filmService.findAllByGenres(genres) else null

        var intersected = filmByRating.intersect(filmByYears).intersect(filmByDuration)

        if (filmByActors != null) intersected = intersected.intersect(filmByActors)

        if (filmByCreators != null) intersected = intersected.intersect(filmByCreators)

        if (filmByGenres != null) intersected = intersected.intersect(filmByGenres)

        return if (intersected.isEmpty()) ResponseEntity(HttpStatus.OK)
        else ResponseEntity(filmService.findFilmIdNameByIds(intersected), HttpStatus.OK)
    }
}
package ru.db_catalog.server.music

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/music/filter")
class MusicFilterController(
    val musicService: MusicService
) {

    @GetMapping
    fun filterMusic(
        @RequestParam(value = "genres", required = false) genres: Set<Long>?,
        @RequestParam(value = "artists", required = false) artists: Set<Long>?,
        @RequestParam(value = "duration_down", required = true) durationDown: Int,
        @RequestParam(value = "duration_up", required = true) durationUp: Int,
        @RequestParam(value = "year_down", required = true) yearDown: Int,
        @RequestParam(value = "year_up", required = true) yearUp: Int,
        @RequestParam(value = "rating_down", required = true) ratingDown: Int,
        @RequestParam(value = "rating_up", required = true) ratingUp: Int,
    ): ResponseEntity<Any> {

        val musicByYears = musicService.getMusicByYears(Pair(yearDown, yearUp))

        val musicByRating = musicService.findAllByRatings(Pair(ratingDown, ratingUp))

        val musicByDuration = musicService.findAllByDuration(durationDown, durationUp)

        val musicByArtists = if (artists != null) musicService.findAllByArtists(artists) else null

        val musicByGenres = if (genres != null) musicService.findAllByGenres(genres) else null

        var intersected = musicByRating.intersect(musicByYears).intersect(musicByDuration)

        if (musicByArtists != null) intersected = intersected.intersect(musicByArtists)

        if (musicByGenres != null) intersected = intersected.intersect(musicByGenres)

        return if (intersected.isEmpty()) ResponseEntity(HttpStatus.OK)
        else ResponseEntity(musicService.findMusicIdNameByIds(intersected), HttpStatus.OK)
    }

}
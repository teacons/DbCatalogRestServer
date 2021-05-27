package ru.db_catalog.server.moderate

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.ErrorCode
import ru.db_catalog.server.book.*
import ru.db_catalog.server.film.*
import ru.db_catalog.server.music.*
import ru.db_catalog.server.people.People
import ru.db_catalog.server.people.PeopleService
import ru.db_catalog.server.top.*

@RestController
@RequestMapping("/api/moderate")
class ModeratorController(
    val bookService: BookService,
    val musicService: MusicService,
    val filmService: FilmService,
    val peopleService: PeopleService,
    val bookTopService: BookTopService,
    val filmTopService: FilmTopService,
    val musicTopService: MusicTopService
) {

    @PostMapping("/book")
    fun addBook(
        @RequestParam(value = "name", required = true) name: String,
        @RequestParam(value = "year", required = true) year: Int,
        @RequestParam(value = "description", required = true) description: String,
        @RequestParam(value = "poster", required = false) poster: String?,
        @RequestParam(value = "book_series", required = false) bookSeriesFrom: String?,
        @RequestParam(value = "authors", required = true) authorsFrom: Set<String>,
        @RequestParam(value = "genres", required = true) genresFrom: Set<Long>
    ): ResponseEntity<Any> {

        val bookSeriesId = if (bookSeriesFrom != null) {
            var bookSeries = bookService.findBookSeriesByName(bookSeriesFrom)

            if (bookSeries == null) {
                bookSeries = BookSeries(null, bookSeriesFrom, null, emptySet())

                bookSeries = bookService.saveBookSeries(bookSeries)
            }

            bookSeries.id
        } else null

        val authors = peopleService.findAllByFullnameIn(authorsFrom).toMutableSet()

        if (authors.size != authorsFrom.size) {
            val diff = mutableSetOf<String>()
            authorsFrom.forEach { s ->
                if (s !in authors.map { it.fullname }) diff.add(s)
            }

            diff.forEach {
                authors.add(peopleService.savePeople(People(null, it, null)))
            }
        }

        val authorsRefs = mutableSetOf<BookAuthorRef>()

        authors.forEach {
            it.id?.let { id -> authorsRefs.add(BookAuthorRef(id)) }
        }

        val genres = bookService.findBookGenresByIds(genresFrom)

        val genresRefs = mutableSetOf<BookGenreRef>()

        genres.forEach {
            genresRefs.add(BookGenreRef(it.id))
        }

        val book = Book(null, name, year, description, poster, bookSeriesId, genresRefs, authorsRefs)

        bookService.saveBook(book)

        return ResponseEntity(ErrorCode(0), HttpStatus.OK)

    }

    @PostMapping("/music")
    fun addMusic(
        @RequestParam(value = "name", required = true) name: String,
        @RequestParam(value = "year", required = true) year: Int,
        @RequestParam(value = "duration", required = true) duration: Int,
        @RequestParam(value = "poster", required = false) poster: String?,
        @RequestParam(value = "artists", required = true) artistsFrom: Set<String>,
        @RequestParam(value = "albums", required = false) albumFrom: String?,
        @RequestParam(value = "genres", required = true) genresFrom: Set<Long>
    ): ResponseEntity<Any> {

        val albumId = if (albumFrom != null) {
            var album = musicService.findMusicAlbumByName(albumFrom)

            if (album == null) {
                album = MusicAlbum(null, albumFrom, year, poster)

                album = musicService.saveMusicAlbum(album)
            }

            album.id
        } else null

        val artists = musicService.findAllArtistsByNameIn(artistsFrom).toMutableSet()

        if (artists.size != artistsFrom.size) {
            val diff = mutableSetOf<String>()
            artistsFrom.forEach { s ->
                if (s !in artists.map { it.name }) diff.add(s)
            }

            diff.forEach {
                artists.add(musicService.saveArtists(Artist(null, it, null)))
            }
        }

        val artistsRefs = mutableSetOf<MusicAuthorRef>()

        artists.forEach {
            it.id?.let { id -> artistsRefs.add(MusicAuthorRef(id)) }
        }

        val genres = musicService.findMusicGenresByIds(genresFrom)

        val genresRefs = mutableSetOf<MusicGenreRef>()

        genres.forEach {
            genresRefs.add(MusicGenreRef(it.id))
        }
        val music = if (albumId != null)
            Music(null, name, year, duration, genresRefs, artistsRefs, setOf(MusicAlbumRef(albumId)))
        else
            Music(null, name, year, duration, genresRefs, artistsRefs, emptySet())
        musicService.saveMusic(music)

        return ResponseEntity(ErrorCode(0), HttpStatus.OK)

    }

    @PostMapping("/film")
    fun addFilm(
        @RequestParam(value = "name", required = true) name: String,
        @RequestParam(value = "year", required = true) year: Int,
        @RequestParam(value = "duration", required = true) duration: Int,
        @RequestParam(value = "description", required = true) description: String,
        @RequestParam(value = "poster", required = false) poster: String?,
        @RequestParam(value = "film_series", required = false) filmSeriesFrom: String?,
        @RequestParam(value = "book", required = false) bookFrom: String?,
        @RequestParam(value = "music", required = false) musicFrom: String?,
        @RequestParam(value = "peoples", required = true) peoplesFrom: String,
        @RequestParam(value = "genres", required = true) genresFrom: Set<Long>
    ): ResponseEntity<Any> {

        val peoplesMap = ObjectMapper().readValue(peoplesFrom, object : TypeReference<Map<String, Long>>() {})


        val filmSeriesId = if (filmSeriesFrom != null) {
            var filmSeries = filmService.findFilmSeriesByName(filmSeriesFrom)

            if (filmSeries == null) {
                filmSeries = FilmSeries(null, filmSeriesFrom, null, emptySet())

                filmSeries = filmService.saveFilmSeries(filmSeries)
            }

            filmSeries.id
        } else null

        val peoples = peopleService.findAllByFullnameIn(peoplesMap.map { it.key }.toSet()).toMutableSet()

        if (peoples.size != peoplesMap.size) {
            val diff = mutableSetOf<String>()
            peoplesMap.keys.forEach { s ->
                if (s !in peoples.map { it.fullname }) diff.add(s)
            }

            diff.forEach {
                peoples.add(peopleService.savePeople(People(null, it, null)))
            }
        }

        val peoplesRefs = mutableSetOf<FilmPeopleRef>()

        peoples.forEach {
            it.id?.let { id -> peoplesRefs.add(FilmPeopleRef(id, peoplesMap[it.fullname]!!)) }
        }

        val genres = filmService.findFilmGenresByIds(genresFrom)

        val genresRefs = mutableSetOf<FilmGenreRef>()

        genres.forEach {
            genresRefs.add(FilmGenreRef(it.id))
        }

        val book = bookFrom?.let { bookService.findBookByName(it) }
            ?: return ResponseEntity(ErrorCode(10), HttpStatus.OK)

        val musics = mutableSetOf<FilmMusicRef>()

//        musicFrom.forEach {
//            musics.add(musicService.findMusicByName(it))
//        }

        if (musicFrom != null) {
            musicService.findMusicByName(musicFrom)?.let { musics.add(FilmMusicRef(it.id!!)) }
            if (musics.isEmpty()) return ResponseEntity(ErrorCode(11), HttpStatus.OK)
        }

        val film =
            Film(
                null,
                name,
                year,
                duration,
                description,
                poster,
                filmSeriesId,
                book.id,
                genresRefs,
                peoplesRefs,
                musics.toSet()
            )

        filmService.saveFilm(film)

        return ResponseEntity(ErrorCode(0), HttpStatus.OK)

    }

    @PostMapping("/top/{type}")
    fun addTop(
        @PathVariable type: String,
        @RequestParam(value = "name", required = true) name: String,
        @RequestParam(value = "contents", required = true) contentsFrom: String
    ): ResponseEntity<Any> {

        val contents = ObjectMapper().readValue(contentsFrom, object : TypeReference<Map<Int, String>>() {})

        when (type) {

            "book" -> {
                val topRefs = mutableSetOf<BookTopRef>()
                contents.forEach{
                    val book = bookService.findBookByName(it.value) ?: return ResponseEntity(ErrorCode(12), HttpStatus.OK)
                    topRefs.add(BookTopRef(book.id!!, it.key))
                }

                bookTopService.saveTop(BookTop(null, name, topRefs))
            }

            "film" -> {
                val topRefs = mutableSetOf<FilmTopRef>()
                contents.forEach{
                    val film = filmService.findFilmByName(it.value) ?: return ResponseEntity(ErrorCode(13), HttpStatus.OK)
                    topRefs.add(FilmTopRef(film.id!!, it.key))
                }

                filmTopService.saveTop(FilmTop(null, name, topRefs))
            }

            "music" -> {
                val topRefs = mutableSetOf<MusicTopRef>()
                contents.forEach{
                    val music = musicService.findMusicByName(it.value) ?: return ResponseEntity(ErrorCode(14), HttpStatus.OK)
                    topRefs.add(MusicTopRef(music.id!!, it.key))
                }

                musicTopService.saveTop(MusicTop(null, name, topRefs))
            }

        }

        return ResponseEntity(ErrorCode(0), HttpStatus.OK)

    }

}
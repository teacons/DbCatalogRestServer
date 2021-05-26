package ru.db_catalog.server.moderate

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.db_catalog.server.book.*
import ru.db_catalog.server.film.FilmService
import ru.db_catalog.server.music.*
import ru.db_catalog.server.people.People
import ru.db_catalog.server.people.PeopleService

@RestController
@RequestMapping("/api/moderate")
class ModeratorController(
    val bookService: BookService,
    val musicService: MusicService,
    val filmService: FilmService,
    val peopleService: PeopleService,
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

        var book = Book(null, name, year, description, poster, bookSeriesId, genresRefs, authorsRefs)

        book = bookService.saveBook(book)

        return ResponseEntity(book.id, HttpStatus.OK)

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
        var music = if (albumId != null)
            Music(null, name, year, duration, genresRefs, artistsRefs, setOf(MusicAlbumRef(albumId)))
        else
            Music(null, name, year, duration, genresRefs, artistsRefs, emptySet())
        music = musicService.saveMusic(music)

        return ResponseEntity(music.id, HttpStatus.OK)

    }

//    @PostMapping("/film")
//    fun addFilm(
//        @RequestParam(value = "name", required = true) name: String,
//        @RequestParam(value = "year", required = true) year: Int,
//        @RequestParam(value = "duration", required = true) duration: Int,
//        @RequestParam(value = "poster", required = false) poster: String?,
//        @RequestParam(value = "film_series", required = false) filmSeriesFrom: String?,
//        @RequestParam(value = "book", required = false) book: String?,
//        @RequestParam(value = "music", required = false) music: String?,
//        @RequestParam(value = "peoples", required = true) peoplesFrom: Map<String, Int>,
//        @RequestParam(value = "genres", required = true) genresFrom: Set<Long>
//    ): ResponseEntity<Any> {
//
//        val filmSeriesId = if (filmSeriesFrom != null) {
//            var filmSeries = filmService.findFilmSeriesByName(filmSeriesFrom)
//
//            if (filmSeries == null) {
//                filmSeries = FilmSeries(null, filmSeriesFrom, null, emptySet())
//
//                filmSeries = filmService.saveFilmSeries(filmSeries)
//            }
//
//            filmSeries.id
//        } else null
//
//        val peoples = peopleService.findAllByFullnameIn(peoplesFrom.map { it.key }.toSet()).toMutableSet()
//
//        if (peoples.size != peoplesFrom.size) {
//            val diff = mutableSetOf<String>()
//            peoplesFrom.forEach { s ->
//                if (s !in peoples.map { it.name }) diff.add(s)
//            }
//
//            diff.forEach {
//                peoples.add(peopleService.savePeople(People(null, it, null)))
//            }
//        }
//
//        val artistsRefs = mutableSetOf<MusicAuthorRef>()
//
//        peoples.forEach {
//            it.id?.let { id -> artistsRefs.add(MusicAuthorRef(id)) }
//        }
//
//        val genres = musicService.findMusicGenresByIds(genresFrom)
//
//        val genresRefs = mutableSetOf<MusicGenreRef>()
//
//        genres.forEach {
//            genresRefs.add(MusicGenreRef(it.id))
//        }
//        var music = if (filmSeriesId != null)
//            Music(null, name, year, duration, genresRefs, artistsRefs, setOf(MusicAlbumRef(filmSeriesId)))
//        else
//            Music(null, name, year, duration, genresRefs, artistsRefs, emptySet())
//        music = musicService.saveMusic(music)
//
//        return ResponseEntity(music.id, HttpStatus.OK)
//
//    }
}
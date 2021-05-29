package ru.db_catalog.server.top

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookTopService(val db: BookTopRepository) {

    @Cacheable("bookTopById", key = "#id")
    fun findById(id: Long): Optional<BookTop> = db.findById(id)

    @Cacheable("bookTopByBookId", key = "#id")
    fun findByBookId(id: Long): Set<BookTop> = db.findTopByBookId(id)

    fun findPositionInTop(topId: Long, bookId: Long) = db.findPositionInTop(topId, bookId)

    @Cacheable("allBookTopIdName")
    fun findAllId() = db.findAllId()

    fun saveTop(bookTop: BookTop) = db.save(bookTop)

    @Cacheable("bookTopByName", key = "#name")
    fun findAllMusicsByName(name: String) = db.findIdAllByNameLikeIgnoreCase(name)

}

@Service
class MusicTopService(val db: MusicTopRepository) {

    @Cacheable("musicTopById", key = "#id")
    fun findById(id: Long): Optional<MusicTop> = db.findById(id)

    @Cacheable("musicTopByMusicId", key = "#id")
    fun findByMusicId(id: Long): Set<MusicTop> = db.findTopByMusicId(id)

    fun findPositionInTop(topId: Long, musicId: Long) = db.findPositionInTop(topId, musicId)

    @Cacheable("allMusicTopIdName")
    fun findAllId() = db.findAllId()

    fun saveTop(musicTop: MusicTop) = db.save(musicTop)

    @Cacheable("musicTopByName", key = "#name")
    fun findAllMusicsByName(name: String) = db.findIdAllByNameLikeIgnoreCase(name)

}

@Service
class FilmTopService(val db: FilmTopRepository) {

    @Cacheable("filmTopById", key = "#id")
    fun findById(id: Long): Optional<FilmTop> = db.findById(id)

    @Cacheable("filmTopByFilmId", key = "#id")
    fun findByFilmId(id: Long): Set<FilmTop> = db.findTopByFilmId(id)

    fun findPositionInTop(topId: Long, filmId: Long) = db.findPositionInTop(topId, filmId)

    @Cacheable("allFilmTopIdName")
    fun findAllId() = db.findAllId()

    fun saveTop(filmTop: FilmTop) = db.save(filmTop)

    @Cacheable("filmTopByName", key = "#name")
    fun findAllMusicsByName(name: String) = db.findIdAllByNameLikeIgnoreCase(name)

}
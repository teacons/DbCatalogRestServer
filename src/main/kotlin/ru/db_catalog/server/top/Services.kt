package ru.db_catalog.server.top

import org.springframework.stereotype.Service
import java.util.*

@Service
class BookTopService(val db: BookTopRepository) {

    fun findById(id: Long): Optional<BookTop> = db.findById(id)

    fun findByBookId(id: Long): Set<BookTop> = db.findTopByBookId(id)

    fun findPositionInTop(topId: Long, bookId: Long) = db.findPositionInTop(topId, bookId)

    fun findAllIdName() = db.findAllIdName()


}

@Service
class MusicTopService(val db: MusicTopRepository) {

    fun findById(id: Long): Optional<MusicTop> = db.findById(id)

    fun findByMusicId(id: Long): Set<MusicTop> = db.findTopByMusicId(id)

    fun findPositionInTop(topId: Long, musicId: Long) = db.findPositionInTop(topId, musicId)

    fun findAllIdName() = db.findAllIdName()
}

@Service
class FilmTopService(val db: FilmTopRepository) {

    fun findById(id: Long): Optional<FilmTop> = db.findById(id)

    fun findByFilmId(id: Long): Set<FilmTop> = db.findTopByFilmId(id)

    fun findPositionInTop(topId: Long, filmId: Long) = db.findPositionInTop(topId, filmId)

    fun findAllIdName() = db.findAllIdName()

}
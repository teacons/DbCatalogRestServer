package ru.db_catalog.server.music

import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*

@Service
class MusicGenreService(val db: MusicGenreRepository) {

    fun getMusicGenres(): MutableIterable<MusicGenre> = db.findAll()

    fun getMusicGenre(id: Long): Optional<MusicGenre> = db.findById(id)

    fun existsById(id: Long) = db.existsById(id)

}

@Service
class MusicService(val db: MusicRepository) {

    fun findById(id: Long): Optional<Music> = db.findById(id)

    fun findAllIdName(): Set<ContentIdName> = db.findAllIdName()

    fun getRating(id: Long): Double? = db.getRating(id)

}

@Service
class MusicAlbumService(val db: MusicAlbumRepository) {

    fun findById(id: Long): Optional<MusicAlbum> = db.findById(id)

}

@Service
class ArtistService(val db: ArtistRepository) {

    fun findById(id: Long) = db.findById(id)

}


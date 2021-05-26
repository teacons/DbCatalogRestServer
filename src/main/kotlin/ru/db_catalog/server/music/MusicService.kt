package ru.db_catalog.server.music

import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*

@Service
class MusicService(
    val musicRepository: MusicRepository,
    val artistRepository: ArtistRepository,
    val musicGenreRepository: MusicGenreRepository,
    val musicAlbumRepository: MusicAlbumRepository,
) {

    fun findMusicById(id: Long): Optional<Music> = musicRepository.findById(id)

    fun findAllMusicIdName(): Set<ContentIdName> = musicRepository.findAllIdName()

    fun getMusicRating(id: Long): Double? = musicRepository.getRating(id)

    fun findAllArtists(): MutableIterable<Artist> = artistRepository.findAll()

    fun findArtistById(id: Long): Optional<Artist> = artistRepository.findById(id)

    fun findAllMusicGenres(): MutableIterable<MusicGenre> = musicGenreRepository.findAll()

    fun findMusicGenreById(id: Long): Optional<MusicGenre> = musicGenreRepository.findById(id)

    fun existsMusicGenreById(id: Long) = musicGenreRepository.existsById(id)

    fun findMusicAlbumById(id: Long): Optional<MusicAlbum> = musicAlbumRepository.findById(id)

    fun findMusicAlbumByName(name: String) = musicAlbumRepository.findFirstByName(name)

    fun saveMusicAlbum(musicAlbum: MusicAlbum) = musicAlbumRepository.save(musicAlbum)

    fun saveArtists(artist: Artist) = artistRepository.save(artist)

    fun findAllArtistsByNameIn(names: Set<String>) = artistRepository.findAllByNameIn(names)

    fun findMusicGenresByIds(ids: Set<Long>) = musicGenreRepository.findAllByIdIn(ids)

    fun saveMusic(music: Music) = musicRepository.save(music)


}



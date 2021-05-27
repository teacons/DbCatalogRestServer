package ru.db_catalog.server.music

import org.springframework.cache.annotation.Cacheable
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

    @Cacheable("musicById", key = "#id")
    fun findMusicById(id: Long): Optional<Music> = musicRepository.findById(id)

    @Cacheable("allMusicIdName")
    fun findAllMusicIdName(): Set<ContentIdName> = musicRepository.findAllIdName()

    @Cacheable("musicRating", key = "#id")
    fun getMusicRating(id: Long): Double? = musicRepository.getRating(id)

    @Cacheable("allArtists")
    fun findAllArtists(): MutableIterable<Artist> = artistRepository.findAll()

    @Cacheable("artistById", key = "#id")
    fun findArtistById(id: Long): Optional<Artist> = artistRepository.findById(id)

    @Cacheable("allMusicGenres")
    fun findAllMusicGenres(): MutableIterable<MusicGenre> = musicGenreRepository.findAll()

    @Cacheable("musicGenreById", key = "#id")
    fun findMusicGenreById(id: Long): Optional<MusicGenre> = musicGenreRepository.findById(id)

    @Cacheable("existsMusicGenreById", key = "#id")
    fun existsMusicGenreById(id: Long) = musicGenreRepository.existsById(id)

    @Cacheable("musicAlbumById", key = "#id")
    fun findMusicAlbumById(id: Long): Optional<MusicAlbum> = musicAlbumRepository.findById(id)

    @Cacheable("existsMusicGenreByName", key = "#name")
    fun findMusicAlbumByName(name: String) = musicAlbumRepository.findFirstByName(name)

    fun saveMusicAlbum(musicAlbum: MusicAlbum) = musicAlbumRepository.save(musicAlbum)

    fun saveArtists(artist: Artist) = artistRepository.save(artist)

    fun findAllArtistsByNameIn(names: Set<String>) = artistRepository.findAllByNameIn(names)

    fun findMusicGenresByIds(ids: Set<Long>) = musicGenreRepository.findAllByIdIn(ids)

    fun saveMusic(music: Music) = musicRepository.save(music)

    fun getMusicByYears(years: Pair<Int, Int>) =
        musicRepository.findAllByYearBetween(years.first, years.second)

    fun findAllByRatings(ratings: Pair<Int, Int>) = musicRepository.findAllByRatings(ratings.first, ratings.second)

    fun findAllByDuration(duration: Int, duration2: Int) = musicRepository.findAllByDuration(duration, duration2)

    fun findAllByGenres(genres: Set<Long>) = musicRepository.findAllByGenres(genres)

    fun findAllByArtists(artists: Set<Long>) = musicRepository.findAllByArtists(artists)

    fun findMusicIdNameByIds(ids: Set<Long>): Set<ContentIdName> = musicRepository.findIdNameByIds(ids)

    fun findAllArtistsByIdInContentIdName(ids: Set<Long>) = artistRepository.findAllByIdInContentIdName(ids)

    @Cacheable("musicByName", key = "#name")
    fun findMusicByName(name: String) = musicRepository.findByName(name)

}



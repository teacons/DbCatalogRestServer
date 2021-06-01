package ru.db_catalog.server.music

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.db_catalog.server.Content
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.top.MusicTopService
import ru.db_catalog.server.user.UserService
import java.util.*

@Service
class MusicService(
    val musicRepository: MusicRepository,
    val artistRepository: ArtistRepository,
    val musicGenreRepository: MusicGenreRepository,
    val musicAlbumRepository: MusicAlbumRepository,
    val userService: UserService,
    val musicTopService: MusicTopService
) {

    @Cacheable("musicById", key = "#id")
    fun findMusicById(id: Long): Optional<Music> = musicRepository.findById(id)

    @Cacheable("allMusicIdName")
    fun findAllMusicIds(): Set<Long> = musicRepository.findAllId()

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

    fun findMusicAlbumByName(name: String) = musicAlbumRepository.findFirstByName(name)

    fun saveMusicAlbum(musicAlbum: MusicAlbum) = musicAlbumRepository.save(musicAlbum)

    fun saveArtists(artist: Artist) = artistRepository.save(artist)

    fun findAllArtistsByNameIn(names: Set<String>) = artistRepository.findAllByNameIn(names)

    fun findMusicGenresByIds(ids: Set<Long>) = musicGenreRepository.findAllByIdIn(ids)

    fun saveMusic(music: Music) = musicRepository.save(music)

    fun getMusicByYears(years: Pair<Int, Int>) =
        musicRepository.findAllByYearBetween(years.first, years.second)

    fun findAllByRatings(ratings: Pair<Int, Int>) = musicRepository.findAllByRatings(ratings.first, ratings.second)

    fun findAllRatingIsNull() = musicRepository.findAllRatingIsNull()

    fun findAllByDuration(duration: Pair<Int, Int>) = musicRepository.findAllByDuration(duration.first, duration.second)

    fun findAllByGenres(genres: Set<Long>) = musicRepository.findAllByGenres(genres)

    fun findAllByArtists(artists: Set<Long>) = musicRepository.findAllByArtists(artists)

    fun findAllArtistsByIdInContentIdName(ids: Set<Long>) = artistRepository.findAllByIdInContentIdName(ids)

    fun findMusicByName(name: String) = musicRepository.findByName(name)

    @Cacheable("musicsByName", key = "#name")
    fun findAllMusicsByName(name: String) = musicRepository.findIdAllByNameLikeIgnoreCase(name)

    @Cacheable("filterMusic", key = "#filterMusic.getUUID()")
    fun filterMusic(filterMusic: MusicFilter): Set<Long> {
        val musicByYears = getMusicByYears(filterMusic.years)

        val musicByRating = findAllByRatings(filterMusic.ratings).toMutableSet()

        if (filterMusic.ratings.first == 0)
            musicByRating += findAllRatingIsNull()

        val musicByDuration = findAllByDuration(filterMusic.duration)

        val musicBySearchQuery =
            if (filterMusic.searchQuery != null) findAllMusicsByName("%${filterMusic.searchQuery.toLowerCase()}%") else null

        val musicByArtists = if (filterMusic.artists != null) findAllByArtists(filterMusic.artists) else null

        val musicByGenres = if (filterMusic.genres != null) findAllByGenres(filterMusic.genres) else null

        var intersected = musicByRating.intersect(musicByYears).intersect(musicByDuration)

        if (musicByArtists != null) intersected = intersected.intersect(musicByArtists)

        if (musicByGenres != null) intersected = intersected.intersect(musicByGenres)

        if (musicBySearchQuery != null) intersected = intersected.intersect(musicBySearchQuery)

        return intersected
    }

    fun prepareMusic(music: Music, expanded: Boolean, username: String): ResponseEntity<Any> {

        val userId = userService.findByUsername(username)?.id

        var rating = getMusicRating(music.id!!)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        music.musicGenres.forEach {
            genres.add(findMusicGenreById(it.musicGenreId).get().name)
        }

        val poster =
            music.albums.firstOrNull()?.let { findMusicAlbumById(it.musicAlbumId).get().poster }
        if (!expanded) {
            return ResponseEntity(
                Content(music.id, music.name, music.year, poster, rating, genres),
                HttpStatus.OK
            )
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)

            val albums = mutableSetOf<MusicAlbum>()

            music.albums.forEach {
                albums.add(findMusicAlbumById(it.musicAlbumId).get())
            }

            val artists = mutableSetOf<Artist>()

            music.artists.forEach {
                artists.add(findArtistById(it.artistId).get())
            }

            val viewed = userService.existsViewByUserIdMusicId(userId, music.id)

            val userRating = userService.getUserMusicRating(userId, music.id)

            val top = musicTopService.findByMusicId(music.id).firstOrNull()
            val topIdName = top?.let { ContentIdName(it.id!!, it.name) }
            val topPosition = top?.let { musicTopService.findPositionInTop(it.id!!, music.id) }

            return ResponseEntity(
                MusicForAnswer(
                    music.id,
                    music.name,
                    music.year,
                    music.duration,
                    poster,
                    rating,
                    artists,
                    albums,
                    genres,
                    viewed,
                    userRating,
                    topIdName,
                    topPosition
                ), HttpStatus.OK
            )
        }
    }
}



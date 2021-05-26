package ru.db_catalog.server.music

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName

@Repository
interface MusicGenreRepository : CrudRepository<MusicGenre, Long> {

    fun findAllByIdIn(id: Set<Long>): Set<MusicGenre>

}

@Repository
interface MusicRepository : CrudRepository<Music, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_music where music_id = :id")
    fun getRating(@Param("id") id: Long): Double?

    @Query("select id, name from music")
    fun findAllIdName(): Set<ContentIdName>

    @Query("select id, name from music where id in (:ids)")
    fun findIdNameByIds(ids: Set<Long>): Set<ContentIdName>

    @Query("select id from music where year between :year and :year2")
    fun findAllByYearBetween(year: Int, year2: Int): Set<Long>

    @Query("with tmp as (select distinct music_id, round(avg(rating) OVER (PARTITION BY music_id), 2) as music_rating from user_viewed_music) select music_id from tmp where music_rating >= :down and music_rating <= :up")
    fun findAllByRatings(down: Int, up: Int): Set<Long>

    @Query("select distinct id from music join music_has_artist on music.id = music_has_artist.music_id where artist_id in (:artists)")
    fun findAllByArtists(artists: Set<Long>): Set<Long>

    @Query("select distinct id from music join music_has_music_genre on music.id = music_has_music_genre.music_id where music_has_music_genre.music_genre_id in (:genres)")
    fun findAllByGenres(genres: Set<Long>): Set<Long>

    @Query("select id from music where duration between :duration and :duration2")
    fun findAllByDuration(duration: Int, duration2: Int): Set<Long>

    fun findByName(name: String): Music?

}

@Repository
interface ArtistRepository : CrudRepository<Artist, Long> {

    fun findAllByNameIn(name: Set<String>): Set<Artist>

    @Query("select id, name from artist where id in (:ids)")
    fun findAllByIdInContentIdName(ids: Set<Long>): Set<ContentIdName>

}

@Repository
interface MusicAlbumRepository : CrudRepository<MusicAlbum, Long> {

    fun findFirstByName(name: String): MusicAlbum?

}



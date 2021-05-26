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
    fun getNames(ids: Set<Long>): Set<ContentIdName>

}

@Repository
interface ArtistRepository : CrudRepository<Artist, Long> {

    fun findAllByNameIn(name: Set<String>): Set<Artist>

}

@Repository
interface MusicAlbumRepository : CrudRepository<MusicAlbum, Long> {

    fun findFirstByName(name: String): MusicAlbum?

}



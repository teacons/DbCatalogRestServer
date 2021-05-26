package ru.db_catalog.server.music

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import ru.db_catalog.server.ContentIdName

@Table("music")
data class Music(
    @Id val id: Long?,
    val name: String,
    val year: Int,
    val duration: Int,
    @MappedCollection(idColumn = "music_id")
    val musicGenres: Set<MusicGenreRef>,
    @MappedCollection(idColumn = "music_id")
    val artists: Set<MusicAuthorRef>,
    @MappedCollection(idColumn = "music_id")
    val albums: Set<MusicAlbumRef>
)

@Table("music_has_music_genre")
data class MusicGenreRef(val musicGenreId: Long)

@Table("music_has_artist")
data class MusicAuthorRef(val artistId: Long)

@Table("music_has_album")
data class MusicAlbumRef(val musicAlbumId: Long)

@Table("artist")
data class Artist(@Id val id: Long?, val name: String, val description: String?)

@Table("music_genre")
data class MusicGenre(@Id val id: Long, val name: String, val description: String)

@Table("music_album")
data class MusicAlbum(
    @Id val id: Long?,
    val name: String,
    val year: Int,
    val poster: String?,
)

data class MusicForAnswer(
    val id: Long,
    val name: String,
    val year: Int,
    val duration: Int,
    val poster: String?,
    val rating: Double,
    val artists: Set<Artist>,
    val albums: Set<MusicAlbum>,
    val genres: Set<String>,
    val viewed: Boolean,
    val ratingUser: Double?,
    val top: ContentIdName?,
    val topPosition: Int?
)

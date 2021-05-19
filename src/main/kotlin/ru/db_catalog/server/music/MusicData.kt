package ru.db_catalog.server.music

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("music")
data class Music(
    @Id val id: Long,
    val name: String,
    val year: Int,
    val duration: Int,
    @MappedCollection(idColumn = "music_id")
    val musicGenres: MutableSet<MusicGenreRef> = mutableSetOf(),
    @MappedCollection(idColumn = "music_id")
    val artists: MutableSet<MusicAuthorRef> = mutableSetOf(),
    @MappedCollection(idColumn = "music_id")
    val albums: MutableSet<MusicAlbumRef> = mutableSetOf()
) {
    fun addMusicGenre(musicGenre: MusicGenre) {
        musicGenres.add(MusicGenreRef(musicGenre.id))
    }

    fun addArtist(artist: Artist) {
        this.artists.add(MusicAuthorRef(artist.id))
    }

    fun addAlbum(album: MusicAlbum) {
        this.albums.add(MusicAlbumRef(album.id))
    }
}

@Table("music_has_music_genre")
data class MusicGenreRef(val musicGenreId: Long)

@Table("music_has_artist")
data class MusicAuthorRef(val artistId: Long)

@Table("music_has_album")
data class MusicAlbumRef(val musicAlbumId: Long)

@Table("artist")
data class Artist(@Id val id: Long, val name: String, val description: String?)

@Table("music_genre")
data class MusicGenre(@Id val id: Long, val name: String, val description: String)

@Table("music_album")
data class MusicAlbum(
    @Id val id: Long,
    val name: String,
    val year: Int,
    val poster: String?,
)


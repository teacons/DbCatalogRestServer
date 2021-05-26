package ru.db_catalog.server.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table("user")
data class User(
    @Id val id: Long?,
    var username: String,
    var password: String,
    var email: String,
    val createTime: Timestamp,
    @MappedCollection(idColumn = "id")
    val role: Long,

    @MappedCollection(idColumn = "user_id")
    val likedBookGenres: MutableSet<UserBookGenreRef> = mutableSetOf(),
    @MappedCollection(idColumn = "user_id")
    val likedFilmGenres: MutableSet<UserFilmGenreRef> = mutableSetOf(),
    @MappedCollection(idColumn = "user_id")
    val likedMusicGenres: MutableSet<UserMusicGenreRef> = mutableSetOf(),

    @MappedCollection(idColumn = "user_id")
    val viewedBook: MutableSet<UserViewedBookRef> = mutableSetOf(),
    @MappedCollection(idColumn = "user_id")
    val viewedFilm: MutableSet<UserViewedFilmRef> = mutableSetOf(),
    @MappedCollection(idColumn = "user_id")
    val viewedMusic: MutableSet<UserViewedMusicRef> = mutableSetOf(),
)

data class UserForAnswer(
    val username: String,
    val email: String,
    val createTime: Timestamp
)

@Table("user_likes_book_genre")
data class UserBookGenreRef(val bookGenreId: Long)

@Table("user_likes_film_genre")
data class UserFilmGenreRef(val filmGenreId: Long)

@Table("user_likes_music_genre")
data class UserMusicGenreRef(val musicGenreId: Long)

@Table("user_viewed_book")
data class UserViewedBookRef(val bookId: Long, var rating: Int?, val time: Timestamp)

@Table("user_viewed_film")
data class UserViewedFilmRef(val filmId: Long, var rating: Int?, val time: Timestamp)

@Table("user_viewed_music")
data class UserViewedMusicRef(val musicId: Long, var rating: Int?, val time: Timestamp)

@Table("user_role")
data class RoleEntity(@Id val id: Long, val name: String)
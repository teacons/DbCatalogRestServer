package ru.db_catalog.server.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import ru.db_catalog.server.film.FilmGenre
import ru.db_catalog.server.book.Book
import ru.db_catalog.server.book.BookGenre
import ru.db_catalog.server.film.Film
import ru.db_catalog.server.music.Music
import ru.db_catalog.server.music.MusicGenre
import java.sql.Timestamp

@Table("user")
data class User(
    @Id val id: Long?,
    val username: String,
    val password: String,
    val email: String,
    val createTime: Timestamp,

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
) {

}

@Table("user_likes_book_genre")
data class UserBookGenreRef(val bookGenreId: Long)

@Table("user_likes_film_genre")
data class UserFilmGenreRef(val filmGenreId: Long)

@Table("user_likes_music_genre")
data class UserMusicGenreRef(val musicGenreId: Long)

@Table("user_viewed_book")
data class UserViewedBookRef(val bookId: Long)

@Table("user_viewed_film")
data class UserViewedFilmRef(val filmId: Long)

@Table("user_viewed_music")
data class UserViewedMusicRef(val musicId: Long)
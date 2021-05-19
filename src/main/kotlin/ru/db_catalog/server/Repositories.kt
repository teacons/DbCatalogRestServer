package ru.db_catalog.server

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.book.Book
import ru.db_catalog.server.book.BookGenre
import ru.db_catalog.server.book.BookSeries
import ru.db_catalog.server.book.ContentSimple
import ru.db_catalog.server.film.Film
import ru.db_catalog.server.music.Music
import ru.db_catalog.server.music.MusicAlbum
import ru.db_catalog.server.music.MusicGenre
import ru.db_catalog.server.top.BookTop
import ru.db_catalog.server.user.User

@Repository
interface MusicGenreRepository : CrudRepository<MusicGenre, Long>

@Repository
interface FilmGenreRepository : CrudRepository<FilmGenre, Long>

@Repository
interface BookGenreRepository : CrudRepository<BookGenre, Long> {
//    fun findBookGenreById(id: Long): BookGenreWithoutDescription
}

@Repository
interface BookRepository : CrudRepository<Book, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_book where book_id = :id")
    fun getRating(@Param("id") id: Long): Double?

    @Query("select id, name from book")
    fun findAllIdName(): Set<ContentSimple>

    @Query("select id from book")
    fun findAllIds(): Set<Long>

}

@Repository
interface MusicRepository : CrudRepository<Music, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_music where music_id = :id")
    fun getRating(@Param("id") id: Long): Double?

    @Query("select id, name from music")
    fun findAllIdName(): Set<ContentSimple>

    @Query("select id from music")
    fun findAllIds(): Set<Long>

}

@Repository
interface FilmRepository : CrudRepository<Film, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_film where film_id = :id")
    fun getRating(@Param("id") id: Long): Double?

    @Query("select id, name from film")
    fun findAllIdName(): Set<ContentSimple>

    @Query("select id from film")
    fun findAllIds(): Set<Long>

}

@Repository
interface MusicAlbumRepository : CrudRepository<MusicAlbum, Long>

@Repository
interface BookSeriesRepository : CrudRepository<BookSeries, Long>

@Repository
interface PeopleRepository : CrudRepository<People, Long>

@Repository
interface BookTopRepository : CrudRepository<BookTop, Long> {

    @Query("select * from top where id in (select top_id from top_has_book where book_id = :bookId);")
    fun findTopByBookId(@Param("bookId") bookId: Long): Set<BookTop>

    @Query("select position from top_has_book where top_id = :top_id and book_id = :book_id")
    fun findPositionInTop(@Param("top_id") topId: Long, @Param("book_id") bookId: Long): Int
}

//@Repository
//interface FilmTopRepository : CrudRepository<FilmTop, Long>

//@Repository
//interface MusicTopRepository : CrudRepository<MusicTop, Long>

@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun existsUserByUsername(username: String): Boolean

    fun existsUserByEmail(email: String): Boolean
}

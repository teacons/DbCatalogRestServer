package ru.db_catalog.server

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.book.Book
import ru.db_catalog.server.book.BookGenre
import ru.db_catalog.server.book.BookSeries
import ru.db_catalog.server.film.Film
import ru.db_catalog.server.film.FilmSeries
import ru.db_catalog.server.music.Artist
import ru.db_catalog.server.music.Music
import ru.db_catalog.server.music.MusicAlbum
import ru.db_catalog.server.music.MusicGenre
import ru.db_catalog.server.top.BookTop
import ru.db_catalog.server.top.FilmTop
import ru.db_catalog.server.top.MusicTop
import ru.db_catalog.server.top.TopIdName
import ru.db_catalog.server.user.User

@Repository
interface MusicGenreRepository : CrudRepository<MusicGenre, Long>

@Repository
interface FilmGenreRepository : CrudRepository<FilmGenre, Long>

@Repository
interface BookGenreRepository : CrudRepository<BookGenre, Long>

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
interface ArtistRepository : CrudRepository<Artist, Long> {

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
interface FilmSeriesRepository : CrudRepository<FilmSeries, Long>

@Repository
interface PeopleRepository : CrudRepository<People, Long> {



}

@Repository
interface PeopleFunctionRepository : CrudRepository<PeopleFunction, Long>

@Repository
interface BookTopRepository : CrudRepository<BookTop, Long> {

    @Query("select * from top where id in (select top_id from top_has_book where book_id = :bookId and position <= 10);")
    fun findTopByBookId(@Param("bookId") bookId: Long): Set<BookTop>

    @Query("select position from top_has_book where top_id = :top_id and book_id = :book_id")
    fun findPositionInTop(@Param("top_id") topId: Long, @Param("book_id") bookId: Long): Int

    @Query("select id, name from top where id in (select top_id from top_has_book)")
    fun findAllIdName(): Set<TopIdName>
}

@Repository
interface FilmTopRepository : CrudRepository<FilmTop, Long> {

    @Query("select * from top where id in (select top_id from top_has_film where film_id = :filmId and position <= 10);")
    fun findTopByFilmId(@Param("filmId") filmId: Long): Set<FilmTop>

    @Query("select position from top_has_film where top_id = :top_id and film_id = :film_id")
    fun findPositionInTop(@Param("top_id") topId: Long, @Param("film_id") filmId: Long): Int

    @Query("select id, name from top where id in (select top_id from top_has_film)")
    fun findAllIdName(): Set<TopIdName>

}

@Repository
interface MusicTopRepository : CrudRepository<MusicTop, Long> {

    @Query("select * from top where id in (select top_id from top_has_music where music_id = :musicId and position <= 10);")
    fun findTopByMusicId(@Param("musicId") musicId: Long): Set<MusicTop>

    @Query("select position from top_has_music where top_id = :top_id and music_id = :music_id")
    fun findPositionInTop(@Param("top_id") topId: Long, @Param("music_id") musicId: Long): Int

    @Query("select id, name from top where id in (select top_id from top_has_music)")
    fun findAllIdName(): Set<TopIdName>

}

@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun existsUserByUsername(username: String): Boolean

    fun existsUserByEmail(email: String): Boolean

    @Query("select exists(select * from user_viewed_book where user_id = :userId and book_id = :bookId)")
    fun existsViewByUserIdBookId(@Param("userId") userId: Long, @Param("bookId") bookId: Long): Boolean

    @Query("select rating from user_viewed_book where user_id = :userId and book_id = :bookId")
    fun getUserBookRating(@Param("userId") userId: Long, @Param("bookId") bookId: Long): Double?

    @Query("select exists(select * from user_viewed_music where user_id = :userId and music_id = :musicId)")
    fun existsViewByUserIdMusicId(@Param("userId") userId: Long, @Param("musicId") musicId: Long): Boolean

    @Query("select rating from user_viewed_music where user_id = :userId and music_id = :musicId")
    fun getUserMusicRating(@Param("userId") userId: Long, @Param("musicId") musicId: Long): Double?

    @Query("select exists(select * from user_viewed_film where user_id = :userId and film_id = :filmId)")
    fun existsViewByUserIdFilmId(@Param("userId") userId: Long, @Param("filmId") filmId: Long): Boolean

    @Query("select rating from user_viewed_film where user_id = :userId and film_id = :filmId")
    fun getUserFilmRating(@Param("userId") userId: Long, @Param("filmId") filmId: Long): Double?

}



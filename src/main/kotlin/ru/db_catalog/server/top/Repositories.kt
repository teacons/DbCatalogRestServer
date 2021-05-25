package ru.db_catalog.server.top

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName

@Repository
interface BookTopRepository : CrudRepository<BookTop, Long> {

    @Query("select * from top where id in (select top_id from top_has_book where book_id = :bookId and position <= 10);")
    fun findTopByBookId(@Param("bookId") bookId: Long): Set<BookTop>

    @Query("select position from top_has_book where top_id = :top_id and book_id = :book_id")
    fun findPositionInTop(@Param("top_id") topId: Long, @Param("book_id") bookId: Long): Int

    @Query("select id, name from top where id in (select top_id from top_has_book)")
    fun findAllIdName(): Set<ContentIdName>
}

@Repository
interface FilmTopRepository : CrudRepository<FilmTop, Long> {

    @Query("select * from top where id in (select top_id from top_has_film where film_id = :filmId and position <= 10);")
    fun findTopByFilmId(@Param("filmId") filmId: Long): Set<FilmTop>

    @Query("select position from top_has_film where top_id = :top_id and film_id = :film_id")
    fun findPositionInTop(@Param("top_id") topId: Long, @Param("film_id") filmId: Long): Int

    @Query("select id, name from top where id in (select top_id from top_has_film)")
    fun findAllIdName(): Set<ContentIdName>

}

@Repository
interface MusicTopRepository : CrudRepository<MusicTop, Long> {

    @Query("select * from top where id in (select top_id from top_has_music where music_id = :musicId and position <= 10);")
    fun findTopByMusicId(@Param("musicId") musicId: Long): Set<MusicTop>

    @Query("select position from top_has_music where top_id = :top_id and music_id = :music_id")
    fun findPositionInTop(@Param("top_id") topId: Long, @Param("music_id") musicId: Long): Int

    @Query("select id, name from top where id in (select top_id from top_has_music)")
    fun findAllIdName(): Set<ContentIdName>

}
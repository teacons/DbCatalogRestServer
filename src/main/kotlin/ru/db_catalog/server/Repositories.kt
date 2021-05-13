package ru.db_catalog.server

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface MusicGenreRepository : CrudRepository<MusicGenre, String> {

    @Query("select * from db.music_genre")
    fun getMusicGenres(): List<MusicGenre>

}

interface FilmGenreRepository : CrudRepository<FilmGenre, String> {

    @Query("select * from db.film_genre")
    fun getFilmGenres(): List<FilmGenre>

}

interface BookGenreRepository : CrudRepository<BookGenre, String> {

    @Query("select * from db.book_genre")
    fun getBookGenres(): List<BookGenre>

}


interface BookRepository : CrudRepository<Book, Long>


interface BookSeriesRepository : CrudRepository<BookSeries, Long>
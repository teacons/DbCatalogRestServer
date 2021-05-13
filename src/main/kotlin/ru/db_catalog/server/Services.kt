package ru.db_catalog.server

import org.springframework.stereotype.Service
import java.util.*

@Service
class MusicGenreService(val db: MusicGenreRepository) {

    fun getMusicGenres(): List<MusicGenre> = db.getMusicGenres()

}

@Service
class FilmGenreService(val db: FilmGenreRepository) {

    fun getFilmGenres(): List<FilmGenre> = db.getFilmGenres()

}

@Service
class BookGenreService(val db: BookGenreRepository) {

    fun getBookGenres(): List<BookGenre> = db.getBookGenres()

}

@Service
class BookSeriesService(val db: BookSeriesRepository) {

    fun findById(id: Long): Optional<BookSeries> = db.findById(id)

}

@Service
class BookService(val db: BookRepository) {

    fun findById(id: Long): Optional<Book> = db.findById(id)

    fun findAll(): MutableIterable<Book> = db.findAll()

}
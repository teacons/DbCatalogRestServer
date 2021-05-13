package ru.db_catalog.server

import org.springframework.stereotype.Service
import java.util.*

@Service
class MusicGenreService(val db: MusicGenreRepository) {

    fun getMusicGenres(): MutableIterable<MusicGenre> = db.findAll()

    fun getMusicGenre(id: Long): Optional<MusicGenre> = db.findById(id)

}

@Service
class FilmGenreService(val db: FilmGenreRepository) {

    fun getFilmGenres(): MutableIterable<FilmGenre> = db.findAll()

    fun getFilmGenre(id: Long): Optional<FilmGenre> = db.findById(id)

}

@Service
class BookGenreService(val db: BookGenreRepository) {

    fun getBookGenres(): MutableIterable<BookGenre> = db.findAll()

    fun getBookGenre(id: Long): Optional<BookGenre> = db.findById(id)

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

@Service
class PeopleService(val db: PeopleRepository) {

    fun findById(id: Long): Optional<People> = db.findById(id)

    fun findAll(): MutableIterable<People> = db.findAll()

}
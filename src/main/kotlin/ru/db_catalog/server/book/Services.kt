package ru.db_catalog.server.book

import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*

@Service
class BookGenreService(val db: BookGenreRepository) {

    fun getBookGenres(): MutableIterable<BookGenre> = db.findAll()

    fun getBookGenre(id: Long): Optional<BookGenre> = db.findById(id)

}

@Service
class BookService(val db: BookRepository) {

    fun findById(id: Long): Optional<Book> = db.findById(id)

    fun findAllIdName(): Set<ContentIdName> = db.findAllIdName()

    fun getRating(id: Long): Double? = db.getRating(id)

}

@Service
class BookSeriesService(val db: BookSeriesRepository) {

    fun findById(id: Long): Optional<BookSeries> = db.findById(id)

}



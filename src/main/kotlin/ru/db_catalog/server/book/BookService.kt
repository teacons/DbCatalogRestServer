package ru.db_catalog.server.book

import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*


@Service
class BookService(
    val bookRepository: BookRepository,
    val bookPeopleRepository: BookPeopleRepository,
    val bookGenreRepository: BookGenreRepository,
    val bookSeriesRepository: BookSeriesRepository
) {

    fun findBookById(id: Long): Optional<Book> = bookRepository.findById(id)

    fun findAllBookIdName(): Set<ContentIdName> = bookRepository.findAllIdName()

    fun getBookRating(id: Long): Double? = bookRepository.getRating(id)

    fun findAllBookPeoples(): MutableIterable<BookAuthorRef> = bookPeopleRepository.findAll()

    fun findAllBookGenres(): MutableIterable<BookGenre> = bookGenreRepository.findAll()

    fun findBookGenreById(id: Long): Optional<BookGenre> = bookGenreRepository.findById(id)

    fun existsBookGenreById(id: Long): Boolean = bookGenreRepository.existsById(id)

    fun findBookSeriesById(id: Long): Optional<BookSeries> = bookSeriesRepository.findById(id)

}



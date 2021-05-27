package ru.db_catalog.server.book

import org.springframework.cache.annotation.Cacheable
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

    @Cacheable("bookById", key = "#id")
    fun findBookById(id: Long): Optional<Book> = bookRepository.findById(id)

    @Cacheable("allBookIdName")
    fun findAllBookIdName(): Set<ContentIdName> = bookRepository.findAllIdName()

    fun findBookIdNameByIds(ids: Set<Long>): Set<ContentIdName> = bookRepository.findIdNameByIds(ids)

    @Cacheable("bookRating", key = "#id")
    fun getBookRating(id: Long): Double? = bookRepository.getRating(id)

    fun getBooksBetweenYears(years: Pair<Int, Int>) =
        bookRepository.findAllByYearBetween(years.first, years.second)

    fun findAllByRatings(ratings: Pair<Int, Int>) = bookRepository.findAllByRatings(ratings.first, ratings.second)

    fun findAllByAuthors(authors: Set<Long>) = bookRepository.findAllByAuthors(authors)

    fun findAllByGenres(genres: Set<Long>) = bookRepository.findAllByGenres(genres)

    @Cacheable("allBookPeoples")
    fun findAllBookPeoples(): MutableIterable<BookAuthorRef> = bookPeopleRepository.findAll()

    @Cacheable("allBookGenres")
    fun findAllBookGenres(): MutableIterable<BookGenre> = bookGenreRepository.findAll()

    @Cacheable("bookGenreById", key = "#id")
    fun findBookGenreById(id: Long): Optional<BookGenre> = bookGenreRepository.findById(id)

    @Cacheable("existsBookGenreById", key = "#id")
    fun existsBookGenreById(id: Long): Boolean = bookGenreRepository.existsById(id)

    @Cacheable("bookSeriesById", key = "#id")
    fun findBookSeriesById(id: Long): Optional<BookSeries> = bookSeriesRepository.findById(id)

    @Cacheable("bookSeriesByName", key = "#name")
    fun findBookSeriesByName(name: String): BookSeries? = bookSeriesRepository.findFirstByName(name)

    fun findBookGenresByIds(ids: Set<Long>): Set<BookGenre> = bookGenreRepository.findAllByIdIn(ids)

    fun saveBookSeries(bookSeries: BookSeries) = bookSeriesRepository.save(bookSeries)

    fun saveBook(book: Book) = bookRepository.save(book)

    @Cacheable("bookByName", key = "#name")
    fun findBookByName(name: String) = bookRepository.findFirstByName(name)

}



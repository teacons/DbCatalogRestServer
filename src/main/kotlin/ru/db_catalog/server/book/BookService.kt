package ru.db_catalog.server.book

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.db_catalog.server.Content
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.people.People
import ru.db_catalog.server.people.PeopleService
import ru.db_catalog.server.top.BookTopService
import ru.db_catalog.server.user.UserService
import java.util.*

@Service
class BookService(
    val bookRepository: BookRepository,
    val bookPeopleRepository: BookPeopleRepository,
    val bookGenreRepository: BookGenreRepository,
    val bookSeriesRepository: BookSeriesRepository,
    val peopleService: PeopleService,
    val userService: UserService,
    val bookTopService: BookTopService
) {

    @Cacheable("bookById", key = "#id")
    fun findBookById(id: Long): Optional<Book> = bookRepository.findById(id)

    @Cacheable("allBookId")
    fun findAllBookIds(): Set<Long> = bookRepository.findAllId()

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

    fun findBookSeriesByName(name: String): BookSeries? = bookSeriesRepository.findFirstByName(name)

    fun findBookGenresByIds(ids: Set<Long>): Set<BookGenre> = bookGenreRepository.findAllByIdIn(ids)

    fun saveBookSeries(bookSeries: BookSeries) = bookSeriesRepository.save(bookSeries)

    fun saveBook(book: Book) = bookRepository.save(book)

    fun findBookByName(name: String) = bookRepository.findFirstByName(name)

    @Cacheable("booksByName", key = "#name")
    fun findAllBooksIdsByName(name: String) = bookRepository.findIdAllByNameLikeIgnoreCase(name)

    @Cacheable("filterBook", key = "#filterBook.getUUID()")
    fun filterBook(filterBook: FilterBook): Set<Long> {

        val bookByYears = getBooksBetweenYears(filterBook.years)

        val bookByRating = findAllByRatings(filterBook.ratings)

        val bookByAuthors = if (filterBook.authors != null) findAllByAuthors(filterBook.authors) else null

        val bookByGenres = if (filterBook.genres != null) findAllByGenres(filterBook.genres) else null

        var intersected = bookByRating.intersect(bookByYears)

        if (bookByAuthors != null) intersected = intersected.intersect(bookByAuthors)

        if (bookByGenres != null) intersected = intersected.intersect(bookByGenres)

        return intersected
    }

    fun prepareBook(book: Book, expanded: Boolean, username: String): ResponseEntity<Any> {

        val userId = userService.findByUsername(username)?.id

        var rating = getBookRating(book.id!!)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        book.bookGenres.forEach {
            genres.add(findBookGenreById(it.bookGenreId).get().name)
        }

        if (!expanded) {
            return ResponseEntity(
                Content(book.id, book.name, book.year, book.poster, rating, genres),
                HttpStatus.OK
            )
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)
            val bookSeriesWithoutBooks = if (book.bookSeriesId != null) {
                val bookSeries = findBookSeriesById(book.bookSeriesId).get()

                ContentIdName(bookSeries.id!!, bookSeries.name)
            } else null

            val authors = mutableSetOf<People>()

            book.authors.forEach {
                authors.add(peopleService.findPeopleById(it.peopleId).get())
            }

            val viewed = userService.existsViewByUserIdBookId(userId, book.id)

            val userRating = userService.getUserBookRating(userId, book.id)

            val top = bookTopService.findByBookId(book.id).firstOrNull()
            val topIdName = top?.let { ContentIdName(it.id!!, it.name) }
            val topPosition = top?.let { bookTopService.findPositionInTop(it.id!!, book.id) }

            return ResponseEntity(
                BookForAnswer(
                    book.id,
                    book.name,
                    book.year,
                    book.description,
                    book.poster,
                    rating,
                    bookSeriesWithoutBooks,
                    authors,
                    genres,
                    viewed,
                    userRating,
                    topIdName,
                    topPosition
                ), HttpStatus.OK
            )

        }

    }

}



package ru.db_catalog.server.book

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName

@Repository
interface BookGenreRepository : CrudRepository<BookGenre, Long> {

    fun findAllByIdIn(id: Set<Long>): Set<BookGenre>

}

@Repository
interface BookRepository : CrudRepository<Book, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_book where book_id = :id")
    fun getRating(@Param("id") id: Long): Double?

    @Query("select id, name from book")
    fun findAllIdName(): Set<ContentIdName>

    @Query("select id, name from book where id in (:ids)")
    fun findIdNameByIds(@Param("ids") ids: Set<Long>): Set<ContentIdName>

    @Query("select id from book where year between :year and :year2")
    fun findAllByYearBetween(year: Int, year2: Int): Set<Long>

    @Query("with tmp as (select book_id, round(avg(rating), 2) as rating from user_viewed_book where rating >= :down and rating <= :up group by book_id) select book_id from tmp")
    fun findAllByRatings(@Param("down") down: Int, @Param("up") up: Int): Set<Long>

    @Query("select distinct id from book join book_has_people on book.id = book_has_people.book_id where people_id in (:authors)")
    fun findAllByAuthors(@Param("authors") authors: Set<Long>): Set<Long>

    @Query("select distinct id from book join book_has_book_genre on book.id = book_has_book_genre.book_id where book_has_book_genre.book_genre_id in (:genres)")
    fun findAllByGenres(@Param("genres") genres: Set<Long>): Set<Long>

}

@Repository
interface BookSeriesRepository : CrudRepository<BookSeries, Long> {

    fun findFirstByName(name: String): BookSeries?

}

@Repository
interface BookPeopleRepository : CrudRepository<BookAuthorRef, Long>
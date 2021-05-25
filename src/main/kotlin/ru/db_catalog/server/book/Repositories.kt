package ru.db_catalog.server.book

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName

@Repository
interface BookGenreRepository : CrudRepository<BookGenre, Long>

@Repository
interface BookRepository : CrudRepository<Book, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_book where book_id = :id")
    fun getRating(@Param("id") id: Long): Double?

    @Query("select id, name from book")
    fun findAllIdName(): Set<ContentIdName>

    @Query("select id, name from book where id in (:ids)")
    fun getNames(ids: Set<Long>): Set<ContentIdName>

}

@Repository
interface BookSeriesRepository : CrudRepository<BookSeries, Long>

@Repository
interface BookPeopleRepository : CrudRepository<BookAuthorRef, Long>
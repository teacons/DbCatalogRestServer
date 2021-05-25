package ru.db_catalog.server.film

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName

@Repository
interface FilmGenreRepository : CrudRepository<FilmGenre, Long>

@Repository
interface FilmRepository : CrudRepository<Film, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_film where film_id = :id")
    fun getRating(@Param("id") id: Long): Double?

    @Query("select id, name from film")
    fun findAllIdName(): Set<ContentIdName>

    @Query("select id, name from film where id in (:ids)")
    fun getNames(ids: Set<Long>): Set<ContentIdName>

}

@Repository
interface FilmSeriesRepository : CrudRepository<FilmSeries, Long>

@Repository
interface FilmPeopleRepository : CrudRepository<FilmPeopleRef, Long>
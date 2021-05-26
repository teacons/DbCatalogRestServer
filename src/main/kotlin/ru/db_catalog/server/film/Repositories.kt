package ru.db_catalog.server.film

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.book.BookGenre

@Repository
interface FilmGenreRepository : CrudRepository<FilmGenre, Long> {

    fun findAllByIdIn(id: Set<Long>): Set<FilmGenre>

}

@Repository
interface FilmRepository : CrudRepository<Film, Long> {

    @Query("select round(avg(rating), 2) from user_viewed_film where film_id = :id")
    fun getRating(id: Long): Double?

    @Query("select id, name from film")
    fun findAllIdName(): Set<ContentIdName>

    @Query("select id, name from film where id in (:ids)")
    fun findIdNameByIds(ids: Set<Long>): Set<ContentIdName>

    @Query("select id from film where year between :year and :year2")
    fun findAllByYearBetween(year: Int, year2: Int): Set<Long>

    @Query("with tmp as (select distinct film_id, round(avg(rating) OVER (PARTITION BY film_id), 2) as film_rating from user_viewed_film) select film_id from tmp where film_rating >= :down and film_rating <= :up")
    fun findAllByRatings(down: Int, up: Int): Set<Long>

    @Query("select distinct id from film join film_has_people on film.id = film_has_people.film_id where people_id in (:authors) and people_function_id = 2")
    fun findAllByActors(authors: Set<Long>): Set<Long>

    @Query("select distinct id from film join film_has_people on film.id = film_has_people.film_id where people_id in (:authors) and people_function_id != 2")
    fun findAllByCreators(authors: Set<Long>): Set<Long>

    @Query("select distinct id from film join film_has_film_genre on film.id = film_has_film_genre.film_id where film_has_film_genre.film_genre_id in (:genres)")
    fun findAllByGenres(genres: Set<Long>): Set<Long>

    @Query("select id from film where duration between :duration and :duration2")
    fun findAllByDuration(duration: Int, duration2: Int): Set<Long>

}

@Repository
interface FilmSeriesRepository : CrudRepository<FilmSeries, Long> {

    fun findFirstByName(name: String): FilmSeries?

}

@Repository
interface FilmPeopleRepository : CrudRepository<FilmPeopleRef, Long> {

    fun findAllByPeopleFunctionId(peopleFunctionId: Long): Set<FilmPeopleRef>

    fun findAllByPeopleFunctionIdNot(peopleFunctionId: Long): Set<FilmPeopleRef>

}

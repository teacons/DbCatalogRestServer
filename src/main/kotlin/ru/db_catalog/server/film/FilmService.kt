package ru.db_catalog.server.film

import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*

@Service
class FilmService(
    val filmRepository: FilmRepository,
    val filmPeopleRepository: FilmPeopleRepository,
    val filmGenreRepository: FilmGenreRepository,
    val filmSeriesRepository: FilmSeriesRepository
) {

    fun findFilmById(id: Long): Optional<Film> = filmRepository.findById(id)

    fun findAllFilmsIdName(): Set<ContentIdName> = filmRepository.findAllIdName()

    fun findFilmIdNameByIds(ids: Set<Long>): Set<ContentIdName> = filmRepository.findIdNameByIds(ids)

    fun getFilmRating(id: Long): Double? = filmRepository.getRating(id)

    fun getBooksBetweenYears(years: Pair<Int, Int>) =
        filmRepository.findAllByYearBetween(years.first, years.second)

    fun findAllByRatings(ratings: Pair<Int, Int>) = filmRepository.findAllByRatings(ratings.first, ratings.second)

    fun findAllByAuthors(authors: Set<Long>) = filmRepository.findAllByAuthors(authors)

    fun findAllByGenres(genres: Set<Long>) = filmRepository.findAllByGenres(genres)

    fun findAllByDuration(duration: Int, duration2: Int) = filmRepository.findAllByDuration(duration, duration2)

    fun findAllFilmPeople(): MutableIterable<FilmPeopleRef> = filmPeopleRepository.findAll()

    fun findAllFilmGenres(): MutableIterable<FilmGenre> = filmGenreRepository.findAll()

    fun findFilmGenreById(id: Long): Optional<FilmGenre> = filmGenreRepository.findById(id)

    fun existsFilmGenreById(id: Long) = filmGenreRepository.existsById(id)

    fun findFilmSeriesById(id: Long): Optional<FilmSeries> = filmSeriesRepository.findById(id)

}

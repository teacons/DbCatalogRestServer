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

    fun getFilmRating(id: Long): Double? = filmRepository.getRating(id)

    fun findAllFilmPeople(): MutableIterable<FilmPeopleRef> = filmPeopleRepository.findAll()

    fun findAllFilmGenres(): MutableIterable<FilmGenre> = filmGenreRepository.findAll()

    fun findFilmGenreById(id: Long): Optional<FilmGenre> = filmGenreRepository.findById(id)

    fun existsFilmGenreById(id: Long) = filmGenreRepository.existsById(id)

    fun findFilmSeriesById(id: Long): Optional<FilmSeries> = filmSeriesRepository.findById(id)

}

package ru.db_catalog.server.film

import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*

@Service
class FilmGenreService(val db: FilmGenreRepository) {

    fun getFilmGenres(): MutableIterable<FilmGenre> = db.findAll()

    fun getFilmGenre(id: Long): Optional<FilmGenre> = db.findById(id)

    fun existsById(id: Long) = db.existsById(id)

}

@Service
class FilmService(val db: FilmRepository, val filmPeopleRepository: FilmPeopleRepository) {

    fun findById(id: Long): Optional<Film> = db.findById(id)

    fun findAllIdName(): Set<ContentIdName> = db.findAllIdName()

    fun getRating(id: Long): Double? = db.getRating(id)

    fun findAll(): MutableIterable<FilmPeopleRef> = filmPeopleRepository.findAll()


}

@Service
class FilmSeriesService(val db: FilmSeriesRepository) {

    fun findById(id: Long): Optional<FilmSeries> = db.findById(id)

}


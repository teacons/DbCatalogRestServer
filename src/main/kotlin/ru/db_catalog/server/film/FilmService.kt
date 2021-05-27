package ru.db_catalog.server.film

import org.springframework.cache.annotation.Cacheable
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

    @Cacheable("filmById", key = "#id")
    fun findFilmById(id: Long): Optional<Film> = filmRepository.findById(id)

    @Cacheable("allFilmIdName")
    fun findAllFilmsIdName(): Set<ContentIdName> = filmRepository.findAllIdName()

    fun findFilmIdNameByIds(ids: Set<Long>): Set<ContentIdName> = filmRepository.findIdNameByIds(ids)

    @Cacheable("filmRating", key = "#id")
    fun getFilmRating(id: Long): Double? = filmRepository.getRating(id)

    fun getFilmsBetweenYears(years: Pair<Int, Int>) =
        filmRepository.findAllByYearBetween(years.first, years.second)

    fun findFilmsByRatings(ratings: Pair<Int, Int>) = filmRepository.findAllByRatings(ratings.first, ratings.second)

    fun findAllByActors(actors: Set<Long>) = filmRepository.findAllByActors(actors)

    fun findAllByCreators(creators: Set<Long>) = filmRepository.findAllByCreators(creators)

    fun findAllByGenres(genres: Set<Long>) = filmRepository.findAllByGenres(genres)

    fun findAllByDuration(duration: Int, duration2: Int) = filmRepository.findAllByDuration(duration, duration2)

    @Cacheable("filmPeopleWithoutPeopleFunction", key = "#functionId")
    fun findAllFilmPeopleWithoutPeopleFunction(functionId: Long): Set<FilmPeopleRef> = filmPeopleRepository.findAllByPeopleFunctionIdNot(functionId)

    @Cacheable("filmPeopleByPeopleFunction", key = "#functionId")
    fun findAllFilmPeopleByPeopleFunction(functionId: Long): Set<FilmPeopleRef> = filmPeopleRepository.findAllByPeopleFunctionId(functionId)

    @Cacheable("allFilmGenres")
    fun findAllFilmGenres(): MutableIterable<FilmGenre> = filmGenreRepository.findAll()

    @Cacheable("filmGenreById", key = "#id")
    fun findFilmGenreById(id: Long): Optional<FilmGenre> = filmGenreRepository.findById(id)

    fun findFilmGenresByIds(ids: Set<Long>): Set<FilmGenre> = filmGenreRepository.findAllByIdIn(ids)

    @Cacheable("existsFilmGenreById", key = "#id")
    fun existsFilmGenreById(id: Long) = filmGenreRepository.existsById(id)

    @Cacheable("filmSeriesById", key = "#id")
    fun findFilmSeriesById(id: Long): Optional<FilmSeries> = filmSeriesRepository.findById(id)

    @Cacheable("filmSeriesByName", key = "#name")
    fun findFilmSeriesByName(name: String) = filmSeriesRepository.findFirstByName(name)

    fun saveFilmSeries(filmSeries: FilmSeries) = filmSeriesRepository.save(filmSeries)

    fun saveFilm(film: Film) = filmRepository.save(film)

    @Cacheable("filmByName", key = "#name")
    fun findFilmByName(name: String) = filmRepository.findFirstByName(name)

}

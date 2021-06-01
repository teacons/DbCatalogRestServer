package ru.db_catalog.server.film

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.db_catalog.server.Content
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.music.MusicService
import ru.db_catalog.server.people.PeopleService
import ru.db_catalog.server.people.PeopleWithFunction
import ru.db_catalog.server.top.FilmTopService
import ru.db_catalog.server.user.UserService
import java.util.*

@Service
class FilmService(
    val filmRepository: FilmRepository,
    val filmPeopleRepository: FilmPeopleRepository,
    val filmGenreRepository: FilmGenreRepository,
    val filmSeriesRepository: FilmSeriesRepository,
    val bookService: BookService,
    val musicService: MusicService,
    val peopleService: PeopleService,
    val userService: UserService,
    val filmTopService: FilmTopService
) {

    @Cacheable("filmById", key = "#id")
    fun findFilmById(id: Long): Optional<Film> = filmRepository.findById(id)

    @Cacheable("allFilmId")
    fun findAllFilmIds(): Set<Long> = filmRepository.findAllId()

    fun getFilmRating(id: Long): Double? = filmRepository.getRating(id)

    fun getFilmsBetweenYears(years: Pair<Int, Int>) =
        filmRepository.findAllByYearBetween(years.first, years.second)

    fun findFilmsByRatings(ratings: Pair<Int, Int>) = filmRepository.findAllByRatings(ratings.first, ratings.second)

    fun findAllByActors(actors: Set<Long>) = filmRepository.findAllByActors(actors)

    fun findAllByCreators(creators: Set<Long>) = filmRepository.findAllByCreators(creators)

    fun findAllByGenres(genres: Set<Long>) = filmRepository.findAllByGenres(genres)

    fun findAllByDuration(duration: Pair<Int, Int>) = filmRepository.findAllByDuration(duration.first, duration.second)

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

    fun findFilmSeriesByName(name: String) = filmSeriesRepository.findFirstByName(name)

    fun saveFilmSeries(filmSeries: FilmSeries) = filmSeriesRepository.save(filmSeries)

    fun saveFilm(film: Film) = filmRepository.save(film)

    fun findFilmByName(name: String) = filmRepository.findFirstByName(name)

    @Cacheable("filmsByName", key = "#name")
    fun findAllFilmsByName(name: String) = filmRepository.findIdAllByNameLikeIgnoreCase(name)

    @Cacheable("filterFilm", key = "#filterFilm.getUUID()")
    fun filterFilm(filterFilm: FilterFilm): Set<Long> {
        val filmByYears = getFilmsBetweenYears(filterFilm.years)

        val filmByRating = findFilmsByRatings(filterFilm.ratings)

        val filmByDuration = findAllByDuration(filterFilm.duration)

        val filmBySearchQuery =
            if (filterFilm.searchQuery != null) findAllFilmsByName("%${filterFilm.searchQuery.toLowerCase()}%") else null

        val filmByActors = if (filterFilm.actors != null) findAllByActors(filterFilm.actors) else null

        val filmByCreators = if (filterFilm.creators != null) findAllByCreators(filterFilm.creators) else null

        val filmByGenres = if (filterFilm.genres != null) findAllByGenres(filterFilm.genres) else null

        var intersected = filmByRating.intersect(filmByYears).intersect(filmByDuration)

        if (filmByActors != null) intersected = intersected.intersect(filmByActors)

        if (filmByCreators != null) intersected = intersected.intersect(filmByCreators)

        if (filmByGenres != null) intersected = intersected.intersect(filmByGenres)

        if (filmBySearchQuery != null) intersected = intersected.intersect(filmBySearchQuery)

        return intersected
    }

    fun prepareFilm(film: Film, expanded: Boolean, username: String): ResponseEntity<Any> {

        val userId = userService.findByUsername(username)?.id

        var rating = getFilmRating(film.id!!)

        if (rating == null) rating = 0.0

        val genres = mutableSetOf<String>()

        film.filmGenres.forEach {
            genres.add(findFilmGenreById(it.filmGenreId).get().name)
        }
        if (!expanded) {
            return ResponseEntity(
                Content(film.id, film.name, film.year, film.poster, rating, genres),
                HttpStatus.OK
            )
        } else {
            if (userId == null) return ResponseEntity(HttpStatus.BAD_REQUEST)

            val filmSeries = if (film.filmSeriesId != null) {
                val filmSeries = findFilmSeriesById(film.filmSeriesId).get()

                ContentIdName(filmSeries.id!!, filmSeries.name)
            } else null

            val book = film.bookId?.let {
                val bookTemp = bookService.findBookById(it).get()
                ContentIdName(bookTemp.id!!, bookTemp.name)
            }

            val musics = mutableSetOf<ContentIdName>()

            film.musics.forEach {
                val tempMusic = musicService.findMusicById(it.musicId).get()
                musics.add(ContentIdName(tempMusic.id!!, tempMusic.name))
            }

            val peoples = mutableSetOf<PeopleWithFunction>()

            film.peoples.forEach {
                val people = peopleService.findPeopleById(it.peopleId).get()
                val peopleFunction = peopleService.findPeopleFunctionById(it.peopleFunctionId).get()
                peoples.add(PeopleWithFunction(people.id!!, people.fullname, people.yearOfBirth, peopleFunction.name))

            }

            val viewed = userService.existsViewByUserIdFilmId(userId, film.id)

            val userRating = userService.getUserFilmRating(userId, film.id)

            val top = filmTopService.findByFilmId(film.id).firstOrNull()
            val topIdName = top?.let { ContentIdName(it.id!!, it.name) }
            val topPosition = top?.let { filmTopService.findPositionInTop(it.id!!, film.id) }


            return ResponseEntity(
                FilmForAnswer(
                    film.id,
                    film.name,
                    film.year,
                    film.duration,
                    film.description,
                    film.poster,
                    rating,
                    filmSeries,
                    book,
                    musics,
                    peoples,
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

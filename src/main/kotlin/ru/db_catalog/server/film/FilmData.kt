package ru.db_catalog.server.film

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import ru.db_catalog.server.FilmGenre
import ru.db_catalog.server.People
import ru.db_catalog.server.book.Book

@Table("film")
data class Film(
    @Id val id: Long,
    val name: String,
    val year: Int,
    val duration: Int,
    val description: String,
    val poster: String?,
    @MappedCollection(idColumn = "id")
    val book: Book?,
    @MappedCollection(idColumn = "film_id")
    val filmGenres: MutableSet<FilmGenreRef> = mutableSetOf(),
    @MappedCollection(idColumn = "people_id")
    val peoples: MutableSet<FilmPeopleRef> = mutableSetOf(),
) {
    fun addFilmGenre(filmGenre: FilmGenre) {
        filmGenres.add(FilmGenreRef(filmGenre.id))
    }

    fun addPeople(people: People) {
        this.peoples.add(FilmPeopleRef(people.id))
    }

}

@Table("film_series")
data class FilmSeries(
    @Id val id: Long, val name: String, val description: String?,
    @MappedCollection(idColumn = "film_series_id", keyColumn = "film_series_id")
    val books: List<Film>
)

@Table("film_has_film_genre")
data class FilmGenreRef(val filmGenreId: Long)

@Table("film_has_people")
data class FilmPeopleRef(val peopleId: Long)
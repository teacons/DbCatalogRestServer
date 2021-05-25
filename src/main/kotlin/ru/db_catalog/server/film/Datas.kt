package ru.db_catalog.server.film

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.book.Book
import ru.db_catalog.server.people.PeopleWithFunction

@Table("film")
data class Film(
    @Id val id: Long,
    val name: String,
    val year: Int,
    val duration: Int,
    val description: String,
    val poster: String?,
    val filmSeriesId: Long?,
    @MappedCollection(idColumn = "id")
    val book: Book?,
    @MappedCollection(idColumn = "film_id")
    val filmGenres: MutableSet<FilmGenreRef> = mutableSetOf(),
    @MappedCollection(idColumn = "film_id")
    val peoples: MutableSet<FilmPeopleRef> = mutableSetOf(),
    @MappedCollection(idColumn = "film_id")
    val musics: MutableSet<FilmMusicRef> = mutableSetOf()
)

@Table("film_series")
data class FilmSeries(
    @Id val id: Long, val name: String, val description: String?,
    @MappedCollection(idColumn = "film_series_id", keyColumn = "film_series_id")
    val books: List<Film>
)

@Table("film_has_film_genre")
data class FilmGenreRef(val filmGenreId: Long)

@Table("film_has_people")
data class FilmPeopleRef(val peopleId: Long, val peopleFunctionId: Long)

@Table("film_has_music")
data class FilmMusicRef(val musicId: Long)

data class FilmForAnswer(
    val id: Long,
    val name: String,
    val year: Int,
    val duration: Int,
    val description: String,
    val poster: String?,
    val rating: Double,
    val filmSeries: ContentIdName?,
    val book: ContentIdName?,
    val music: Set<ContentIdName>,
    val peoples: Set<PeopleWithFunction>,
    val genres: Set<String>,
    val viewed: Boolean,
    val ratingUser: Double?,
    val top: ContentIdName?,
    val topPosition: Int?
)

@Table("film_genre")
data class FilmGenre(@Id val id: Long, val name: String, val description: String)
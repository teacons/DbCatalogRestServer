package ru.db_catalog.server.film

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import ru.db_catalog.server.ContentIdName
import ru.db_catalog.server.people.PeopleWithFunction
import java.util.*

@Table("film")
data class Film(
    @Id val id: Long?,
    val name: String,
    val year: Int,
    val duration: Int,
    val description: String,
    val poster: String?,
    val filmSeriesId: Long?,
    val bookId: Long?,
    @MappedCollection(idColumn = "film_id")
    val filmGenres: Set<FilmGenreRef>,
    @MappedCollection(idColumn = "film_id")
    val peoples: Set<FilmPeopleRef>,
    @MappedCollection(idColumn = "film_id")
    val musics: Set<FilmMusicRef>
)

@Table("film_series")
data class FilmSeries(
    @Id val id: Long?, val name: String, val description: String?,
    @MappedCollection(idColumn = "film_series_id", keyColumn = "film_series_id")
    val films: Set<Film>
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

data class FilterFilm(
    val genres: Set<Long>?,
    val actors: Set<Long>?,
    val creators: Set<Long>?,
    val duration: Pair<Int, Int>,
    val years: Pair<Int, Int>,
    val ratings: Pair<Int, Int>,
    val searchQuery: String?
) {
    fun getUUID(): String {
        val genresString = genres?.joinToString()
        val actorsString = actors?.joinToString()
        val creatorsString = creators?.joinToString()
        val durationString = "${duration.first} - ${duration.second}"
        val yearsString = "${years.first} - ${years.second}"
        val ratingsString = "${ratings.first} - ${ratings.second}"

        val string =
            "${genresString}_${actorsString}_${creatorsString}_${durationString}_${yearsString}_${ratingsString}_${searchQuery}"

        val uuid = UUID.nameUUIDFromBytes(string.toByteArray())

        return uuid.toString()
    }
}
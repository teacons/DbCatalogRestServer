package ru.db_catalog.server

import org.springframework.stereotype.Service
import ru.db_catalog.server.book.Book
import ru.db_catalog.server.book.BookGenre
import ru.db_catalog.server.book.BookSeries
import ru.db_catalog.server.film.Film
import ru.db_catalog.server.film.FilmSeries
import ru.db_catalog.server.music.Music
import ru.db_catalog.server.music.MusicAlbum
import ru.db_catalog.server.music.MusicGenre
import ru.db_catalog.server.top.BookTop
import ru.db_catalog.server.top.FilmTop
import ru.db_catalog.server.top.MusicTop
import ru.db_catalog.server.user.User
import java.util.*

@Service
class MusicGenreService(val db: MusicGenreRepository) {

    fun getMusicGenres(): MutableIterable<MusicGenre> = db.findAll()

    fun getMusicGenre(id: Long): Optional<MusicGenre> = db.findById(id)

}

@Service
class FilmGenreService(val db: FilmGenreRepository) {

    fun getFilmGenres(): MutableIterable<FilmGenre> = db.findAll()

    fun getFilmGenre(id: Long): Optional<FilmGenre> = db.findById(id)

}

@Service
class BookGenreService(val db: BookGenreRepository) {

    fun getBookGenres(): MutableIterable<BookGenre> = db.findAll()

    fun getBookGenre(id: Long): Optional<BookGenre> = db.findById(id)

//    fun getBookGenreWithoutDescription(id: Long) = db.findBookGenreById(id)

}

@Service
class BookSeriesService(val db: BookSeriesRepository) {

    fun findById(id: Long): Optional<BookSeries> = db.findById(id)

}

@Service
class BookService(val db: BookRepository) {

    fun findById(id: Long): Optional<Book> = db.findById(id)

    fun findAll(): MutableIterable<Book> = db.findAll()

    fun findAllIdName(): Set<ContentSimple> = db.findAllIdName()

    fun getRating(id: Long): Double? = db.getRating(id)

}

@Service
class PeopleService(val db: PeopleRepository) {

    fun findById(id: Long) = db.findById(id)

    fun findAll(): MutableIterable<People> = db.findAll()

}

@Service
class PeopleFunctionService(val db: PeopleFunctionRepository) {

    fun findById(id: Long) = db.findById(id)

}

@Service
class MusicService(val db: MusicRepository) {

    fun findById(id: Long): Optional<Music> = db.findById(id)

    fun findAll(): MutableIterable<Music> = db.findAll()

    fun findAllIdName(): Set<ContentSimple> = db.findAllIdName()

    fun getRating(id: Long): Double? = db.getRating(id)
}

@Service
class MusicAlbumService(val db: MusicAlbumRepository) {

    fun findById(id: Long): Optional<MusicAlbum> = db.findById(id)

    fun findAll(): MutableIterable<MusicAlbum> = db.findAll()

}

@Service
class ArtistService(val db: ArtistRepository) {

    fun findById(id: Long) = db.findById(id)

}

@Service
class FilmService(val db: FilmRepository) {

    fun findById(id: Long): Optional<Film> = db.findById(id)

    fun findAll(): MutableIterable<Film> = db.findAll()

    fun findAllIdName(): Set<ContentSimple> = db.findAllIdName()

    fun getRating(id: Long): Double? = db.getRating(id)
}

@Service
class FilmSeriesService(val db: FilmSeriesRepository) {

    fun findById(id: Long): Optional<FilmSeries> = db.findById(id)

}

@Service
class BookTopService(val db: BookTopRepository) {

    fun findById(id: Long): Optional<BookTop> = db.findById(id)

    fun findAll(): MutableIterable<BookTop> = db.findAll()

    fun findByBookId(id: Long): Set<BookTop> = db.findTopByBookId(id)

    fun findPositionInTop(topId: Long, bookId: Long) = db.findPositionInTop(topId, bookId)

    fun findAllIdName() = db.findAllIdName()


}

@Service
class FilmTopService(val db: FilmTopRepository) {

    fun findById(id: Long): Optional<FilmTop> = db.findById(id)

    fun findAll(): MutableIterable<FilmTop> = db.findAll()

    fun findByFilmId(id: Long): Set<FilmTop> = db.findTopByFilmId(id)

    fun findPositionInTop(topId: Long, filmId: Long) = db.findPositionInTop(topId, filmId)

    fun findAllIdName() = db.findAllIdName()

}

@Service
class MusicTopService(val db: MusicTopRepository) {

    fun findById(id: Long): Optional<MusicTop> = db.findById(id)

    fun findAll(): MutableIterable<MusicTop> = db.findAll()

    fun findByMusicId(id: Long): Set<MusicTop> = db.findTopByMusicId(id)

    fun findPositionInTop(topId: Long, musicId: Long) = db.findPositionInTop(topId, musicId)

    fun findAllIdName() = db.findAllIdName()
}

@Service
class UserService(val db: UserRepository) {

    fun findById(id: Long): Optional<User> = db.findById(id)

    fun findAll(): MutableIterable<User> = db.findAll()

    fun findByUsername(username: String) = db.findByUsername(username)

    fun save(user: User) = db.save(user)

    fun existsUserByUsername(username: String) = db.existsUserByUsername(username)

    fun existsUserByEmail(email: String) = db.existsUserByEmail(email)

    fun existsViewByUserIdBookId(userId: Long, bookId: Long) = db.existsViewByUserIdBookId(userId, bookId)

    fun getUserBookRating(userId: Long, bookId: Long) = db.getUserBookRating(userId, bookId)

    fun existsViewByUserIdMusicId(userId: Long, musicId: Long): Boolean = db.existsViewByUserIdMusicId(userId, musicId)

    fun getUserMusicRating(userId: Long, musicId: Long) = db.getUserMusicRating(userId, musicId)

    fun existsViewByUserIdFilmId(userId: Long, filmId: Long): Boolean = db.existsViewByUserIdFilmId(userId, filmId)

    fun getUserFilmRating(userId: Long, filmId: Long) = db.getUserFilmRating(userId, filmId)



}
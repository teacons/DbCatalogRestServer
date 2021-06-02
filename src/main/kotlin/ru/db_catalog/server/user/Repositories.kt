package ru.db_catalog.server.user

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName

@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun existsUserByUsername(username: String): Boolean

    fun existsUserByEmail(email: String): Boolean

    @Query("select exists(select * from user_viewed_book where user_id = :userId and book_id = :bookId)")
    fun existsViewByUserIdBookId(@Param("userId") userId: Long, @Param("bookId") bookId: Long): Boolean

    @Query("select rating from user_viewed_book where user_id = :userId and book_id = :bookId")
    fun getUserBookRating(@Param("userId") userId: Long, @Param("bookId") bookId: Long): Double?

    @Query("select exists(select * from user_viewed_music where user_id = :userId and music_id = :musicId)")
    fun existsViewByUserIdMusicId(@Param("userId") userId: Long, @Param("musicId") musicId: Long): Boolean

    @Query("select rating from user_viewed_music where user_id = :userId and music_id = :musicId")
    fun getUserMusicRating(@Param("userId") userId: Long, @Param("musicId") musicId: Long): Double?

    @Query("select exists(select * from user_viewed_film where user_id = :userId and film_id = :filmId)")
    fun existsViewByUserIdFilmId(@Param("userId") userId: Long, @Param("filmId") filmId: Long): Boolean

    @Query("select rating from user_viewed_film where user_id = :userId and film_id = :filmId")
    fun getUserFilmRating(@Param("userId") userId: Long, @Param("filmId") filmId: Long): Double?

    @Query("select distinct id from book join book_has_book_genre on book.id = book_has_book_genre.book_id where book_genre_id in (:genres) and book.id not in (:viewed)")
    fun getUserRecommendedBookWithViewed(@Param("genres") genres: Set<Long>, @Param("viewed") viewed: Set<Long>): Set<Long>

    @Query("select distinct id from book join book_has_book_genre on book.id = book_has_book_genre.book_id where book_genre_id in (:genres)")
    fun getUserRecommendedBook(@Param("genres") genres: Set<Long>): Set<Long>

    @Query("select distinct id from film join film_has_film_genre on film.id = film_has_film_genre.film_id where film_genre_id in (:genres) and film.id not in (:viewed)")
    fun getUserRecommendedFilmWithViewed(@Param("genres") genres: Set<Long>, @Param("viewed") viewed: Set<Long>): Set<Long>

    @Query("select distinct id from film join film_has_film_genre on film.id = film_has_film_genre.film_id where film_genre_id in (:genres)")
    fun getUserRecommendedFilm(@Param("genres") genres: Set<Long>): Set<Long>

    @Query("select distinct id from music join music_has_music_genre on music.id = music_has_music_genre.music_id where music_genre_id in (:genres) and music.id not in (:viewed)")
    fun getUserRecommendedMusicWithViewed(@Param("genres") genres: Set<Long>, @Param("viewed") viewed: Set<Long>): Set<Long>

    @Query("select distinct id from music join music_has_music_genre on music.id = music_has_music_genre.music_id where music_genre_id in (:genres)")
    fun getUserRecommendedMusic(@Param("genres") genres: Set<Long>): Set<Long>


}

@Repository
interface UserRoleRepository : CrudRepository<RoleEntity, Long>

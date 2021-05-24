package ru.db_catalog.server.user

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

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

}

@Repository
interface UserRoleRepository : CrudRepository<RoleEntity, Long>
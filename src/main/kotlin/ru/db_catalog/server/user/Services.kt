package ru.db_catalog.server.user

import org.springframework.stereotype.Service

@Service
class UserService(val db: UserRepository) {

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
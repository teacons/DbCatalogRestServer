package ru.db_catalog.server.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) {

    fun findByUsername(username: String) = userRepository.findByUsername(username)

    fun saveWithEncrypt(user: User): User {
        val password = user.password
        val encryptedPassword = passwordEncoder.encode(password)
        user.password = encryptedPassword
        return userRepository.save(user)
    }

    fun save(user: User) = userRepository.save(user)

    fun findByUsernameAndPassword(username: String, password: String): User? {
        val user = userRepository.findByUsername(username)
        user?.let {
            if (passwordEncoder.matches(password, it.password)) return it

        }
        return null
    }

    fun existsUserByUsername(username: String) = userRepository.existsUserByUsername(username)

    fun existsUserByEmail(email: String) = userRepository.existsUserByEmail(email)

    fun existsViewByUserIdBookId(userId: Long, bookId: Long) = userRepository.existsViewByUserIdBookId(userId, bookId)

    fun getUserBookRating(userId: Long, bookId: Long) = userRepository.getUserBookRating(userId, bookId)

    fun existsViewByUserIdMusicId(userId: Long, musicId: Long): Boolean =
        userRepository.existsViewByUserIdMusicId(userId, musicId)

    fun getUserMusicRating(userId: Long, musicId: Long) = userRepository.getUserMusicRating(userId, musicId)

    fun existsViewByUserIdFilmId(userId: Long, filmId: Long): Boolean =
        userRepository.existsViewByUserIdFilmId(userId, filmId)

    fun getUserFilmRating(userId: Long, filmId: Long) = userRepository.getUserFilmRating(userId, filmId)

}

@Service
class RoleService(val db: UserRoleRepository) {

    fun findById(id: Long) = db.findById(id)

}
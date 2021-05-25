package ru.db_catalog.server.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.db_catalog.server.AuthResponse
import ru.db_catalog.server.JwtProvider
import ru.db_catalog.server.book.BookService
import ru.db_catalog.server.film.FilmService
import ru.db_catalog.server.music.MusicService
import java.sql.Timestamp
import java.util.*


@RestController
@RequestMapping("/api/user")
class UserController(
    val userService: UserService,
    val jwtProvider: JwtProvider
) {

    @GetMapping("/auth")
    fun authUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String
    ): AuthResponse {
        val user = userService.findByUsernameAndPassword(username, password)
        val token = user?.let { jwtProvider.generateToken(it.username) }
        return AuthResponse(token, user?.role)
    }

    @PostMapping("/reg")
    fun registerUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String,
        @RequestParam(value = "email", required = true)
        email: String
    ): UserRegisterAnswerAndChange {
        when {
            userService.existsUserByUsername(username) -> {
                return UserRegisterAnswerAndChange(1)
            }
            username.length > 20 -> {
                return UserRegisterAnswerAndChange(2)
            }
            userService.existsUserByEmail(email) -> {
                return UserRegisterAnswerAndChange(3)
            }
            password.length > 32 -> {
                return UserRegisterAnswerAndChange(4)
            }
            password.length < 6 -> {
                return UserRegisterAnswerAndChange(5)
            }
            else -> {
                return try {
                    val user = User(null, username, password, email, Timestamp(Calendar.getInstance().timeInMillis), 1)
                    userService.saveWithEncrypt(user)
                    UserRegisterAnswerAndChange(0)
                } catch (e: Exception) {
                    UserRegisterAnswerAndChange(666)
                }
            }
        }
    }

    @GetMapping("/info")
    fun getUserInfo(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val userForAnswer = UserForAnswer(user.username, user.email, user.createTime)
        return ResponseEntity(userForAnswer, HttpStatus.OK)
    }

    @PostMapping("/update/username")
    fun updateUsername(
        @RequestHeader("Authorization") token: String,
        @RequestParam("new_username") newUserName: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        when {
            userService.existsUserByUsername(newUserName) ->
                return ResponseEntity(UserRegisterAnswerAndChange(1), HttpStatus.BAD_REQUEST)

            newUserName.length > 20 ->
                return ResponseEntity(UserRegisterAnswerAndChange(2), HttpStatus.BAD_REQUEST)

            else -> {
                user.username = newUserName
                userService.save(user)
            }
        }
        return ResponseEntity(UserRegisterAnswerAndChange(0), HttpStatus.OK)
    }

    @PostMapping("/update/password")
    fun updatePassword(
        @RequestHeader("Authorization") token: String,
        @RequestParam("old_password") oldPassword: String,
        @RequestParam("new_password") newPassword: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsernameAndPassword(username, oldPassword)
            ?: return ResponseEntity(UserRegisterAnswerAndChange(6), HttpStatus.BAD_REQUEST)

        when {
            newPassword.length > 32 -> {
                return ResponseEntity(UserRegisterAnswerAndChange(4), HttpStatus.BAD_REQUEST)
            }
            newPassword.length < 6 -> {
                return ResponseEntity(UserRegisterAnswerAndChange(5), HttpStatus.BAD_REQUEST)
            }
            else -> {
                user.password = newPassword
                userService.saveWithEncrypt(user)
            }
        }
        return ResponseEntity(UserRegisterAnswerAndChange(0), HttpStatus.OK)
    }

    @PostMapping("/update/email")
    fun updateEmail(
        @RequestHeader("Authorization") token: String,
        @RequestParam("new_email") newEmail: String
    ): ResponseEntity<Any> {
        val username = jwtProvider.getLoginFromToken(token.substring(7))
        val user = userService.findByUsername(username) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        when {
            userService.existsUserByEmail(newEmail) -> {
                return ResponseEntity(UserRegisterAnswerAndChange(3), HttpStatus.BAD_REQUEST)
            }
            else -> {
                user.email = newEmail
                userService.save(user)
            }
        }
        return ResponseEntity(UserRegisterAnswerAndChange(0), HttpStatus.OK)
    }



}


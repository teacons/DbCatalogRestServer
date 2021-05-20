package ru.db_catalog.server.user

import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import java.util.*

@RestController
@RequestMapping("/api/user")
class UserController(val userService: UserService) {

    @GetMapping("/auth")
    fun authUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String
    ): Map<String, Long?> {
        val user = userService.findByUsername(username)
        return if (user != null && user.password == password) mapOf(Pair("uid", user.id))
        else mapOf(Pair("uid", null))
    }

    @PostMapping("/reg")
    fun registerUser(
        @RequestParam(value = "username", required = true)
        username: String,
        @RequestParam(value = "password", required = true)
        password: String,
        @RequestParam(value = "email", required = true)
        email: String
    ): UserRegisterAnswer {
        when {
            userService.existsUserByUsername(username) -> {
                return UserRegisterAnswer(1, null)
            }
            username.length > 20 -> {
                return UserRegisterAnswer(2, null)
            }
            userService.existsUserByEmail(email) -> {
                return UserRegisterAnswer(3, null)
            }
            password.length > 32 -> {
                return UserRegisterAnswer(4, null)
            }
            password.length < 6 -> {
                return UserRegisterAnswer(5, null)
            }
            else -> {
                return try {
                    val user = User(null, username, password, email, Timestamp(Calendar.getInstance().timeInMillis))
                    val savedUser = userService.save(user)
                    UserRegisterAnswer(0, savedUser.id)
                } catch (e: Exception) {
                    UserRegisterAnswer(666, null)

                }
            }
        }
    }

}
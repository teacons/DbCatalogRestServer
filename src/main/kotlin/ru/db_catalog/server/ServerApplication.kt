package ru.db_catalog.server

import com.google.common.cache.CacheBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.lang.Strings.hasText
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import ru.db_catalog.server.user.RoleEntity
import ru.db_catalog.server.user.User
import ru.db_catalog.server.user.UserService
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


@SpringBootApplication
class ServerApplication

@Configuration
@EnableCaching
class CacheConfiguration : CachingConfigurerSupport() {

    override fun cacheManager(): CacheManager? {
        return object : ConcurrentMapCacheManager() {
            override fun createConcurrentMapCache(name: String): Cache {
                return ConcurrentMapCache(
                    name,
                    CacheBuilder.newBuilder()
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .build<Any, Any>().asMap(),
                    false
                )
            }
        }
    }

}

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

@Component
class PasswordEncoder : BCryptPasswordEncoder()

@Configuration
@EnableWebSecurity
class SecurityConfig(val jwtFilter: JwtFilter) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/moderate/*").hasRole("MODERATOR")
            .antMatchers(
                "/api/film**",
                "/api/music**",
                "/api/book**",
                "/api/*genre**",
                "/api/user/genre/**",
                "/api/user/update/**",
                "/api/user/recommended**",
                "/api/*/filter*",
                "/api/user/info**",
                "/api/people/*",
                "/api/top/**",
            ).hasRole("USER")
            .antMatchers("/api/user/auth*", "/api/user/reg*").permitAll()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}

@Component
class JwtProvider {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val jwtSecret =
        "L@1d92z!PwT0GWE83ManjQ%GMBjojwWWEpO*EJc2*qqkqjfQUf%&94gGR^3Y1oV#7uF%#B%Tk!UsLhnUJjuCvajaY2Yt1oA^MFt"

    fun generateToken(login: String): String {
        val date = Date.from(LocalDate.now().plusDays(15).atStartOfDay(ZoneId.systemDefault()).toInstant())
        return Jwts.builder()
            .setSubject(login)
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            log.error("invalid token")
        }
        return false
    }

    fun getLoginFromToken(token: String): String {
        val claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body
        return claims.subject
    }

}


class CustomUserDetails(user: User, roles: Set<RoleEntity>) : UserDetails {

    private val username: String = user.username
    private val password: String = user.password
    private val grantedAuthorities: Collection<GrantedAuthority> = roles.map { SimpleGrantedAuthority(it.name) }

    override fun getAuthorities(): Collection<GrantedAuthority> = grantedAuthorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

@Component
class CustomUserDetailsService(val userService: UserService) : UserDetailsService {

    override fun loadUserByUsername(username: String): CustomUserDetails? {
        val user = userService.findByUsername(username)
        val role = if (user != null) {
            if (user.role == 2L)
                setOf(userService.findRoleById(user.role).get(), userService.findRoleById(1).get())
            else
                setOf(userService.findRoleById(user.role).get())
        } else emptySet()
        return user?.let { CustomUserDetails(it, role) }
    }
}

@Component
class JwtFilter(
    val jwtProvider: JwtProvider,
    val customUserDetailsService: CustomUserDetailsService
) : GenericFilterBean() {

    val authorization = "Authorization"

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        logger.info("do filter...")
        val token = getTokenFromRequest(servletRequest as HttpServletRequest)
        if (token != null && jwtProvider.validateToken(token)) {
            val userLogin = jwtProvider.getLoginFromToken(token)
            val customUserDetails = customUserDetailsService.loadUserByUsername(userLogin)
            val auth = UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails?.authorities)
            SecurityContextHolder.getContext().authentication = auth
        }
        filterChain.doFilter(servletRequest, servletResponse)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearer = request.getHeader(authorization)
        return if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            bearer.substring(7)
        } else null
    }

}
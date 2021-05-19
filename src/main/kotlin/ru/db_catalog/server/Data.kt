package ru.db_catalog.server

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import ru.db_catalog.server.book.BookGenre

//================================= Жанры ============================================




@Table("film_genre")
data class FilmGenre(@Id val id: Long, val name: String, val description: String)



//====================================================================================




@Table("people")
data class People(@Id val id: Long, val fullname: String, val yearOfBirth: Int?)




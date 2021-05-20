package ru.db_catalog.server

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("people")
data class People(@Id val id: Long, val fullname: String, val yearOfBirth: Int?)

data class PeopleWithFunction(val id: Long, val fullname: String, val yearOfBirth: Int?, val function: String)

@Table("people_function")
data class PeopleFunction(@Id val id: Long, val name: String)


data class Content(
    val id: Long,
    val name: String,
    val year: Int,
    val poster: String?,
    val rating: Double,
    val genres: Set<String>,
)

data class ContentIdName(val id: Long, val name: String)
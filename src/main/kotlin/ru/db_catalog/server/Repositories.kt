package ru.db_catalog.server

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MusicGenreRepository : CrudRepository<MusicGenre, Long>

@Repository
interface FilmGenreRepository : CrudRepository<FilmGenre, Long>

@Repository
interface BookGenreRepository : CrudRepository<BookGenre, Long>

@Repository
interface BookRepository : CrudRepository<Book, Long>

@Repository
interface BookSeriesRepository : CrudRepository<BookSeries, Long>

@Repository
interface PeopleRepository : CrudRepository<People, Long>

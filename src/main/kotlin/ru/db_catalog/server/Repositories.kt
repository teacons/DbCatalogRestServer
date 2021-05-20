package ru.db_catalog.server

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PeopleRepository : CrudRepository<People, Long>

@Repository
interface PeopleFunctionRepository : CrudRepository<PeopleFunction, Long>



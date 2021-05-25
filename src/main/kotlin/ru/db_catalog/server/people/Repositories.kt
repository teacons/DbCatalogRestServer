package ru.db_catalog.server.people

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PeopleRepository : CrudRepository<People, Long> {

    fun findAllByIdIn(ids: Set<Long>): Set<People>

    fun findAllByFullnameIn(fullname: Set<String>): Set<People>

}

@Repository
interface PeopleFunctionRepository : CrudRepository<PeopleFunction, Long>
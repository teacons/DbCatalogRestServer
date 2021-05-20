package ru.db_catalog.server

import org.springframework.stereotype.Service

@Service
class PeopleService(val db: PeopleRepository) {

    fun findById(id: Long) = db.findById(id)

}

@Service
class PeopleFunctionService(val db: PeopleFunctionRepository) {

    fun findById(id: Long) = db.findById(id)

}

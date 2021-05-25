package ru.db_catalog.server.people

import org.springframework.stereotype.Service
import java.util.*

@Service
class PeopleService(
    val peopleRepository: PeopleRepository,
    val peopleFunctionRepository: PeopleFunctionRepository
) {

    fun findPeopleById(id: Long): Optional<People> = peopleRepository.findById(id)

    fun findAllPeopleByIdIn(ids: Set<Long>): Set<People> = peopleRepository.findAllByIdIn(ids)

    fun findPeopleFunctionById(id: Long): Optional<PeopleFunction> = peopleFunctionRepository.findById(id)

    fun findAllPeopleFunction(): MutableIterable<PeopleFunction> = peopleFunctionRepository.findAll()

}
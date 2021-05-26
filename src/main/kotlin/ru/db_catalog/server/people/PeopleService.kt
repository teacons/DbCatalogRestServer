package ru.db_catalog.server.people

import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*

@Service
class PeopleService(
    val peopleRepository: PeopleRepository,
    val peopleFunctionRepository: PeopleFunctionRepository
) {

    fun findPeopleById(id: Long): Optional<People> = peopleRepository.findById(id)

    fun findAllPeopleByIdIn(ids: Set<Long>): Set<People> = peopleRepository.findAllByIdIn(ids)

    fun findAllPeopleByIdInContentIdName(ids: Set<Long>): Set<ContentIdName> = peopleRepository.findIdsInContentIdName(ids)

    fun findPeopleFunctionById(id: Long): Optional<PeopleFunction> = peopleFunctionRepository.findById(id)

    fun findAllPeopleFunction(): MutableIterable<PeopleFunction> = peopleFunctionRepository.findAll()

    fun findAllByFullnameIn(fullnames: Set<String>) = peopleRepository.findAllByFullnameIn(fullnames)

    fun savePeople(people: People) = peopleRepository.save(people)

}
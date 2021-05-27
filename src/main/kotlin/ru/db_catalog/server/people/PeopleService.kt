package ru.db_catalog.server.people

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.db_catalog.server.ContentIdName
import java.util.*

@Service
class PeopleService(
    val peopleRepository: PeopleRepository,
    val peopleFunctionRepository: PeopleFunctionRepository
) {

    @Cacheable("peopleById", key = "#id")
    fun findPeopleById(id: Long): Optional<People> = peopleRepository.findById(id)

    fun findAllPeopleByIdInContentIdName(ids: Set<Long>): Set<ContentIdName> = peopleRepository.findIdsInContentIdName(ids)

    @Cacheable("peopleFunctionById", key = "#id")
    fun findPeopleFunctionById(id: Long): Optional<PeopleFunction> = peopleFunctionRepository.findById(id)

    @Cacheable("allPeopleFunction")
    fun findAllPeopleFunction(): MutableIterable<PeopleFunction> = peopleFunctionRepository.findAll()

    fun findAllByFullnameIn(fullnames: Set<String>) = peopleRepository.findAllByFullnameIn(fullnames)

    fun savePeople(people: People) = peopleRepository.save(people)

}
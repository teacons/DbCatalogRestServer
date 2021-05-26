package ru.db_catalog.server.people

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.db_catalog.server.ContentIdName

@Repository
interface PeopleRepository : CrudRepository<People, Long> {

    fun findAllByIdIn(ids: Set<Long>): Set<People>

    fun findAllByFullnameIn(fullname: Set<String>): Set<People>

    @Query("select id, fullname as name from people where id in (:ids)")
    fun findIdsInContentIdName(@Param("ids") ids: Set<Long>): Set<ContentIdName>

}

@Repository
interface PeopleFunctionRepository : CrudRepository<PeopleFunction, Long>
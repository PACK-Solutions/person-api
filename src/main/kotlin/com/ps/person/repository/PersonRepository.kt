package com.ps.person.repository

import com.ps.person.model.Person
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : CrudRepository<Person, Long> {
    fun findByFirstNameAndLastName(firstName: String, lastName: String): List<Person>
}

package com.ps.person.controller

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/persons")
class PersonController(
    private val personRepository: PersonRepository,
) {

    @GetMapping
    fun getAllPersons(): Iterable<Person> = personRepository.findAll()

    @GetMapping("/{id}")
    fun getPersonById(@PathVariable id: Long): Person {
        // Execute the repository call
        return personRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Person with id $id not found")
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPerson(@RequestBody person: Person): Person = personRepository.save(person)

    @PutMapping("/{id}")
    fun updatePerson(@PathVariable id: Long, @RequestBody person: Person): ResponseEntity<Person> {
        if (!personRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }

        // Create a new person with the provided ID to ensure we're updating the correct record
        val updatedPerson = person.copy(id = id)
        return ResponseEntity.ok(personRepository.save(updatedPerson))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePerson(@PathVariable id: Long) {
        if (!personRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person with id $id not found")
        }
        personRepository.deleteById(id)
    }
}

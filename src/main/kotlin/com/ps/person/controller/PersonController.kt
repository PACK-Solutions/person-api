package com.ps.person.controller

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/persons")
class PersonController(
    private val personRepository: PersonRepository,
) {
    private val logger = LoggerFactory.getLogger(PersonController::class.java)

    @GetMapping
    fun getAllPersons(): Iterable<Person> {
        logger.info("Retrieving all persons")
        val persons = personRepository.findAll()
        logger.debug("Retrieved {} persons", persons.count())
        return persons
    }

    @GetMapping("/{id}")
    fun getPersonById(@PathVariable id: Long): Person {
        logger.info("Retrieving person with id: {}", id)

        // Execute the repository call
        val personOptional = personRepository.findById(id)

        if (personOptional.isPresent) {
            logger.debug("Found person with id: {}", id)
            return personOptional.get()
        } else {
            logger.warn("Person with id {} not found", id)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person with id $id not found")
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPerson(@RequestBody person: Person): Person {
        logger.info("Creating new person")
        logger.debug(
            "Person details: firstname={}, lastname={}, dateOfBirth={}",
            person.firstname, person.lastname, person.dateOfBirth
        )

        val savedPerson = personRepository.save(person)
        logger.info("Person created successfully with id: {}", savedPerson.id)

        return savedPerson
    }

    @PutMapping("/{id}")
    fun updatePerson(@PathVariable id: Long, @RequestBody person: Person): ResponseEntity<Person> {
        logger.info("Updating person with id: {}", id)

        if (!personRepository.existsById(id)) {
            logger.warn("Update failed: Person with id {} not found", id)
            return ResponseEntity.notFound().build()
        }

        // Create a new person with the provided ID to ensure we're updating the correct record
        val updatedPerson = person.copy(id = id)

        logger.debug(
            "Updating person details: firstname={}, lastname={}, dateOfBirth={}",
            updatedPerson.firstname, updatedPerson.lastname, updatedPerson.dateOfBirth
        )

        val savedPerson = personRepository.save(updatedPerson)
        logger.info("Person with id {} updated successfully", id)

        return ResponseEntity.ok(savedPerson)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePerson(@PathVariable id: Long) {
        logger.info("Deleting person with id: {}", id)

        if (!personRepository.existsById(id)) {
            logger.warn("Delete failed: Person with id {} not found", id)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person with id $id not found")
        }

        personRepository.deleteById(id)
        logger.info("Person with id {} deleted successfully", id)
    }
}

package com.ps.person.controller

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import com.ps.person.service.PravatarService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/persons")
@Tag(name = "Person", description = "Person management API")
class PersonController(
    private val personRepository: PersonRepository,
    private val pravatarService: PravatarService
) {
    private val logger = LoggerFactory.getLogger(PersonController::class.java)

    @GetMapping
    @Operation(
        summary = "Get all persons",
        description = "Retrieves a list of all persons in the database with their details including avatars"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved all persons",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Person::class))]
            )
        ]
    )
    fun getAllPersons(): Iterable<Person> {
        logger.info("Retrieving all persons")
        val persons = personRepository.findAll()
        logger.debug("Retrieved {} persons", persons.count())

        // Return persons with their stored avatars
        return persons
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get person by ID",
        description = "Retrieves a specific person by their unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved the person",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Person::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Person not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getPersonById(
        @Parameter(description = "ID of the person to retrieve", required = true)
        @PathVariable id: Long
    ): Person {
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

    /**
     * Generates a Pravatar avatar for the person and saves it in the database.
     * This should only be called during user creation.
     *
     * @param person The person to generate the avatar for
     * @return The person with the avatar set
     */
    private fun generateAndSetAvatar(person: Person): Person {
        // Generate a Pravatar URL based on the person's name
        val fullName = "${person.firstName} ${person.lastName}"
        val pravatarUrl = pravatarService.generatePravatarUrl(name = fullName)

        // Fetch the Pravatar image and convert it to base64
        val avatarBase64 = pravatarService.fetchPravatarAsBase64(pravatarUrl)

        // Create a new person with the avatar set
        val personWithAvatar = person.copy(avatar = avatarBase64)

        // Save the person with the avatar to the database
        return personRepository.save(personWithAvatar)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new person",
        description = "Creates a new person record with the provided details and automatically generates an avatar"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Person successfully created",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Person::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun createPerson(
        @Parameter(
            description = "Person object to be created",
            required = true,
            content = [Content(schema = Schema(implementation = Person::class))]
        )
        @RequestBody person: Person
    ): Person {
        logger.info("Creating new person")
        logger.debug(
            "Person details: firstName={}, lastName={}, dateOfBirth={}",
            person.firstName, person.lastName, person.dateOfBirth
        )

        // First save the person to get an ID
        val savedPerson = personRepository.save(person)

        // Generate avatar for the newly created person
        val personWithAvatar = generateAndSetAvatar(savedPerson)
        logger.info("Person created successfully with id: {}", personWithAvatar.id)

        return personWithAvatar
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing person",
        description = "Updates a person's information while preserving their avatar. Returns 404 if the person doesn't exist."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Person successfully updated",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Person::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Person not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun updatePerson(
        @Parameter(description = "ID of the person to update", required = true)
        @PathVariable id: Long,

        @Parameter(
            description = "Updated person object",
            required = true,
            content = [Content(schema = Schema(implementation = Person::class))]
        )
        @RequestBody person: Person
    ): ResponseEntity<Person> {
        logger.info("Updating person with id: {}", id)

        // Find the existing person to get the avatar
        val existingPersonOptional = personRepository.findById(id)

        if (existingPersonOptional.isEmpty) {
            logger.warn("Update failed: Person with id {} not found", id)
            return ResponseEntity.notFound().build()
        }

        val existingPerson = existingPersonOptional.get()

        // Create a new person with the provided ID and preserve the existing avatar
        val updatedPerson = person.copy(id = id, avatar = existingPerson.avatar)

        logger.debug(
            "Updating person details: firstName={}, lastName={}, dateOfBirth={}",
            updatedPerson.firstName, updatedPerson.lastName, updatedPerson.dateOfBirth
        )

        val savedPerson = personRepository.save(updatedPerson)
        logger.info("Person with id {} updated successfully", id)

        return ResponseEntity.ok(savedPerson)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a person",
        description = "Deletes a person record by ID. Returns 404 if the person doesn't exist."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Person successfully deleted"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Person not found",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun deletePerson(
        @Parameter(description = "ID of the person to delete", required = true)
        @PathVariable id: Long
    ) {
        logger.info("Deleting person with id: {}", id)

        if (!personRepository.existsById(id)) {
            logger.warn("Delete failed: Person with id {} not found", id)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person with id $id not found")
        }

        personRepository.deleteById(id)
        logger.info("Person with id {} deleted successfully", id)
    }
}

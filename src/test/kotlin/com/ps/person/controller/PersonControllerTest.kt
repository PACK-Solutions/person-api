package com.ps.person.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import com.ps.person.service.PravatarService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(PersonController::class)
class PersonControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var personRepository: PersonRepository

    @MockitoBean
    private lateinit var pravatarService: PravatarService

    private lateinit var testPerson: Person

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        // Configure ObjectMapper to handle LocalDate
        objectMapper.registerModule(JavaTimeModule())

        // Create a test person with an avatar already set
        // This simulates a person that was already created and has an avatar stored in the database
        testPerson = Person(
            id = 1L,
            firstName = "John",
            lastName = "Doe",
            dateOfBirth = LocalDate.of(1990, 1, 1),
            cityOfBirth = "New York",
            countryOfBirth = "USA",
            nationality = "American",
            avatar = "base64encodedimage"
        )

        // Mock the repository to return our test person with avatar
        `when`(personRepository.findById(1L)).thenReturn(Optional.of(testPerson))

        // Mock the pravatar service for user creation
        doReturn("https://i.pravatar.cc/200?u=testmd5hash")
            .`when`(pravatarService).generatePravatarUrl(null, "John Doe")
        doReturn("base64encodedimage")
            .`when`(pravatarService).fetchPravatarAsBase64(anyString())

        // Mock the repository save method to return the person with avatar
        `when`(personRepository.save(any(Person::class.java))).thenAnswer { invocation ->
            val savedPerson = invocation.getArgument<Person>(0)
            // Return a person with the same fields but ensure the avatar is preserved
            savedPerson.copy()
        }
    }

    @Test
    fun `getPersonById should return person with avatar`() {
        mockMvc.perform(
            get("/api/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.avatar").exists())
            .andExpect(jsonPath("$.avatar").value("base64encodedimage"))
    }

    @Test
    fun `updatePerson should preserve avatar field`() {
        // Create an updated person without an avatar (simulating a client request)
        val updatedPerson = Person(
            id = null, // ID will be set in the controller
            firstName = "John",
            lastName = "Updated",
            dateOfBirth = LocalDate.of(1990, 1, 1),
            cityOfBirth = "San Francisco",
            countryOfBirth = "USA",
            nationality = "American"
            // No avatar field - this simulates the client not sending the avatar
        )

        // Perform the update request
        mockMvc.perform(
            put("/api/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPerson))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Updated"))
            .andExpect(jsonPath("$.cityOfBirth").value("San Francisco"))
            .andExpect(jsonPath("$.avatar").exists())
            .andExpect(jsonPath("$.avatar").value("base64encodedimage"))

        // Verify that the repository save method was called with a person that has the avatar preserved
        val personCaptor = org.mockito.ArgumentCaptor.forClass(Person::class.java)
        verify(personRepository).save(personCaptor.capture())
        val savedPerson = personCaptor.value
        assert(savedPerson.avatar == "base64encodedimage") { "Avatar should be preserved during update" }
    }

    @Test
    fun `createPerson should return 409 when person with same firstName and lastName already exists`() {
        // Create a new person with the same firstName and lastName as the test person
        val newPerson = Person(
            id = null,
            firstName = "John",
            lastName = "Doe",
            dateOfBirth = LocalDate.of(1995, 5, 5),
            cityOfBirth = "Los Angeles",
            countryOfBirth = "USA",
            nationality = "American"
        )

        // Mock the repository to return a list with the test person when findByFirstNameAndLastName is called
        `when`(personRepository.findByFirstNameAndLastName("John", "Doe"))
            .thenReturn(listOf(testPerson))

        // Perform the create request
        mockMvc.perform(
            post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPerson))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.type").value("https://api.person.com/errors/duplicate-person"))
            .andExpect(jsonPath("$.title").value("Duplicate Person"))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.detail").value("Person with the same first name and last name already exists"))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))

        // Verify that the repository findByFirstNameAndLastName method was called
        verify(personRepository).findByFirstNameAndLastName("John", "Doe")
        // Verify that the repository save method was not called
        verify(personRepository, never()).save(any(Person::class.java))
    }
}

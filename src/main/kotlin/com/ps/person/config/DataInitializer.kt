package com.ps.person.config

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import com.ps.person.service.PravatarService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataInitializer(
    private val personRepository: PersonRepository,
    private val pravatarService: PravatarService
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String) {
        // Check if the database is already populated
        if (personRepository.count() == 0L) {
            logger.info("Initializing database with 3 users")
            
            // Create and save 3 users
            createUser(
                "John", 
                "Doe", 
                LocalDate.of(1990, 1, 15), 
                "New York", 
                "USA", 
                "American"
            )
            
            createUser(
                "Jane", 
                "Smith", 
                LocalDate.of(1985, 5, 20), 
                "London", 
                "UK", 
                "British"
            )
            
            createUser(
                "Alice", 
                "Johnson", 
                LocalDate.of(1992, 8, 10), 
                "Sydney", 
                "Australia", 
                "Australian"
            )
            
            logger.info("Database initialization completed")
        } else {
            logger.info("Database already contains data, skipping initialization")
        }
    }
    
    private fun createUser(
        firstName: String,
        lastName: String,
        dateOfBirth: LocalDate,
        cityOfBirth: String,
        countryOfBirth: String,
        nationality: String
    ) {
        // Generate avatar URL based on the user's name
        val fullName = "$firstName $lastName"
        val avatarUrl = pravatarService.generatePravatarUrl(name = fullName)
        
        // Fetch the avatar image and convert it to base64
        val avatarBase64 = pravatarService.fetchPravatarAsBase64(avatarUrl)
        
        // Create the Person object
        val person = Person(
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth,
            cityOfBirth = cityOfBirth,
            countryOfBirth = countryOfBirth,
            nationality = nationality,
            avatar = avatarBase64
        )
        
        // Save the person to the database
        personRepository.save(person)
        logger.info("Created user: $firstName $lastName")
    }
}
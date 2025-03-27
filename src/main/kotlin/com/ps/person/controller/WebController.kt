package com.ps.person.controller

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import com.ps.person.service.PravatarService
import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDate

@Controller
@RequestMapping("/persons")
@Hidden
class WebController(
    private val personRepository: PersonRepository,
    private val pravatarService: PravatarService
) {
    private val logger = LoggerFactory.getLogger(WebController::class.java)

    @GetMapping
    fun index(model: Model): String {
        logger.info("Displaying persons index page")

        // Get all persons and generate avatars for them
        val persons = personRepository.findAll().map { person ->
            generateAndSetAvatar(person)
        }

        model.addAttribute("persons", persons)
        if (!model.containsAttribute("person")) {
            model.addAttribute(
                "person", Person(
                    id = null,
                    firstName = "",
                    lastName = "",
                    dateOfBirth = LocalDate.now(),
                    cityOfBirth = "",
                    countryOfBirth = "",
                    nationality = "",
                    avatar = null
                )
            )
        }
        return "index"
    }

    /**
     * Generates a Pravatar avatar for the person and sets it in the avatar field.
     * If the person already has an avatar, it will be used.
     *
     * @param person The person to generate the avatar for
     * @return The person with the avatar set
     */
    private fun generateAndSetAvatar(person: Person): Person {
        // If the person already has an avatar, use it
        if (!person.avatar.isNullOrBlank()) {
            return person
        }

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

    @GetMapping("/edit/{id}")
    fun editPerson(@PathVariable id: Long, model: Model): String {
        logger.info("Editing person with id: {}", id)
        val personOptional = personRepository.findById(id)

        if (personOptional.isPresent) {
            // Generate avatar for the person if needed
            val person = generateAndSetAvatar(personOptional.get())
            model.addAttribute("person", person)

            // Get all persons and generate avatars for them
            val persons = personRepository.findAll().map { p ->
                generateAndSetAvatar(p)
            }
            model.addAttribute("persons", persons)

            return "index"
        } else {
            logger.warn("Person with id {} not found for editing", id)
            return "redirect:/persons"
        }
    }

    @PostMapping("/save")
    fun savePerson(@ModelAttribute person: Person, redirectAttributes: RedirectAttributes): String {
        try {
            logger.info("Saving person: {}", person)
            val savedPerson = personRepository.save(person)
            logger.info("Person saved successfully with id: {}", savedPerson.id)
            redirectAttributes.addFlashAttribute(
                "successMessage",
                if (person.id == null) "Person created successfully." else "Person updated successfully."
            )
        } catch (e: Exception) {
            logger.error("Error saving person: {}", e.message, e)
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving person: ${e.message}")
            redirectAttributes.addFlashAttribute("person", person)
        }
        return "redirect:/persons"
    }

    @GetMapping("/delete/{id}")
    fun deletePerson(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        logger.info("Deleting person with id: {}", id)

        try {
            if (personRepository.existsById(id)) {
                personRepository.deleteById(id)
                logger.info("Person with id {} deleted successfully", id)
                redirectAttributes.addFlashAttribute("successMessage", "Person deleted successfully.")
            } else {
                logger.warn("Person with id {} not found for deletion", id)
                redirectAttributes.addFlashAttribute("errorMessage", "Person not found.")
            }
        } catch (e: Exception) {
            logger.error("Error deleting person with id {}: {}", id, e.message, e)
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting person: ${e.message}")
        }

        return "redirect:/persons"
    }
}

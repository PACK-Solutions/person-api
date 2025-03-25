package com.ps.person.controller

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDate

@Controller
@RequestMapping("/persons")
class WebController(
    private val personRepository: PersonRepository
) {
    private val logger = LoggerFactory.getLogger(WebController::class.java)

    @GetMapping
    fun index(model: Model): String {
        logger.info("Displaying persons index page")
        model.addAttribute("persons", personRepository.findAll())
        if (!model.containsAttribute("person")) {
            model.addAttribute("person", Person(
                id = null,
                firstname = "",
                lastname = "",
                dateOfBirth = LocalDate.now(),
                cityOfBirth = "",
                countryOfBirth = "",
                nationality = ""
            ))
        }
        return "index"
    }

    @GetMapping("/edit/{id}")
    fun editPerson(@PathVariable id: Long, model: Model): String {
        logger.info("Editing person with id: {}", id)
        val personOptional = personRepository.findById(id)
        
        if (personOptional.isPresent) {
            model.addAttribute("person", personOptional.get())
            model.addAttribute("persons", personRepository.findAll())
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
            redirectAttributes.addFlashAttribute("successMessage", 
                if (person.id == null) "Person created successfully." else "Person updated successfully.")
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
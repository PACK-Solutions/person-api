package com.ps.person.controller

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate

@Controller
@RequestMapping("/")
@Hidden
class HomeController(
    private val personRepository: PersonRepository
) {

    @GetMapping
    fun home(model: Model): String {
        model.addAttribute("persons", personRepository.findAll())
        if (!model.containsAttribute("person")) {
            model.addAttribute(
                "person", Person(
                    id = null,
                    firstName = "",
                    lastName = "",
                    dateOfBirth = LocalDate.now(),
                    cityOfBirth = "",
                    countryOfBirth = "",
                    nationality = ""
                )
            )
        }
        return "index"
    }
}

package com.ps.person.controller

import com.ps.person.model.Person
import com.ps.person.repository.PersonRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate

@Controller
@RequestMapping("/")
class HomeController(
    private val personRepository: PersonRepository
) {

    @GetMapping
    fun home(model: Model): String {
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
}

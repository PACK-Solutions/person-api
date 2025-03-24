package com.ps.person.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("person")
data class Person(
    @Id
    val id: Long? = null,
    val firstname: String,
    val lastname: String,
    val dateOfBirth: LocalDate,
    val cityOfBirth: String,
    val countryOfBirth: String,
    val nationality: String
)
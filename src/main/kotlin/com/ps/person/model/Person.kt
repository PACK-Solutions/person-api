package com.ps.person.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("person")
data class Person(
    @Id
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val cityOfBirth: String,
    val countryOfBirth: String,
    val nationality: String,

    @JsonProperty(access = Access.READ_ONLY)
    val avatar: String? = null
)

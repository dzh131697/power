package com.skilltracker.domain.model

import java.util.UUID

data class Skill(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val value: Int = 0,
    val color: Long = 0xFF4CAF50,
    val strengths: List<String> = emptyList(),
    val improvements: List<String> = emptyList()
)

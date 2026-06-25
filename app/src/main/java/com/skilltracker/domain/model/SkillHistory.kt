package com.skilltracker.domain.model

import java.util.UUID

data class SkillHistory(
    val id: String = UUID.randomUUID().toString(),
    val skillId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val delta: Int = 0,
    val reason: String? = null
)
